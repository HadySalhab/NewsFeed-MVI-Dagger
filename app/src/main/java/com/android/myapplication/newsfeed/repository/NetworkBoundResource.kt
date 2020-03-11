package com.android.myapplication.newsfeed.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.android.myapplication.newsfeed.ui.DataState
import com.android.myapplication.newsfeed.ui.Response
import com.android.myapplication.newsfeed.ui.ResponseType
import com.android.myapplication.newsfeed.util.*
import kotlinx.coroutines.*

//cacheObject: data represented in the db
//responseObject: network response
//viewState

abstract class NetworkBoundResource<ResponseObject, CacheObject, ViewStateType>
    (
    isNetworkAvailable: Boolean, // is their a network connection?
    isNetworkRequest: Boolean // is this a network request?
) {
    protected val result = MediatorLiveData<DataState<ViewStateType>>()
    protected lateinit var job: CompletableJob
    protected lateinit var coroutineScope: CoroutineScope

    init {
        setJob(initNewJob())
        setValue(DataState.loading(isLoading = true))

        if (isNetworkRequest) {
            if (isNetworkAvailable) {
                doNetworkRequest()
            } else {
                onCompleteJob(DataState.error(Response.dialogResponse(UNABLE_TODO_OPERATION_WO_INTERNET)))
            }
        } else {
            doCacheRequest()
        }
    }

    fun doCacheRequest() {
        coroutineScope.launch {
            delay(TESTING_CACHE_DELAY)
            // View data from cache only and return
            createCacheRequestAndReturn()
        }
    }

    fun doNetworkRequest() {
        coroutineScope.launch {

            // simulate a network delay for testing
            delay(TESTING_NETWORK_DELAY)

            withContext(Dispatchers.Main) {

                // make network call
                val apiResponse = createCall()
                result.addSource(apiResponse) { response ->
                    result.removeSource(apiResponse)

                    coroutineScope.launch {
                        handleNetworkCall(response)
                    }
                }
            }
        }

        GlobalScope.launch(Dispatchers.IO) {
            delay(NETWORK_TIMEOUT)

            if (!job.isCompleted) {
                Log.e(TAG, "NetworkBoundResource: JOB NETWORK TIMEOUT.")
                job.cancel(CancellationException(UNABLE_TO_RESOLVE_HOST))
            }
        }
    }

    suspend fun handleNetworkCall(response: GenericApiResponse<ResponseObject>) {

        when (response) {
            is ApiSuccessResponse -> {
                handleApiSuccessResponse(response)
            }
            is ApiErrorResponse -> {
                Log.e(TAG, "NetworkBoundResource: ${response.errorMessage}")
                onCompleteJob(DataState.error(Response.dialogResponse(response.errorMessage)))
            }
            is ApiEmptyResponse -> {
                Log.e(TAG, "NetworkBoundResource: Request returned NOTHING (HTTP 204).")
                onCompleteJob(DataState.error(Response.dialogResponse(ERROR_EMPTY_RESPONSE)))
            }
        }
    }

    fun setValue(dataState: DataState<ViewStateType>) {
        result.value = dataState
    }

    @UseExperimental(InternalCoroutinesApi::class)
    private fun initNewJob() = Job().apply {
        invokeOnCompletion(
            onCancelling = true,
            invokeImmediately = true,
            handler = object : CompletionHandler {
                override fun invoke(cause: Throwable?) {
                    if (job.isCancelled) { //the only way the job is going to cancel is if we reached network timeout and when navGraph is changed
                        var message:String = ERROR_UNKNOWN
                        Log.e(TAG, "NetworkBoundResource: Job has been cancelled.")
                        with(cause?.message) {
                            Log.e(
                                TAG,
                                "NetworkBoundResource: job has been cancelled. ${this}"
                            )
                            if (this == UNABLE_TO_RESOLVE_HOST) {
                                message = ERROR_CHECK_NETWORK_CONNECTION
                            } else {
                                this?.let {
                                    message = it
                                }
                            }
                        }
                        onCompleteJob(DataState.error(Response.toastResponse(message)))
                    } else if (job.isCompleted) {
                        Log.e(TAG, "NetworkBoundResource: Job has been completed.")
                        // Do nothing? Should be handled already
                    }
                }
            })
    }.also {
        coroutineScope = CoroutineScope(Dispatchers.IO + it)
        job = it
    }


    fun onCompleteJob(dataState: DataState<ViewStateType>) {
        GlobalScope.launch(Dispatchers.Main) {
            job.complete() //this will invoke the invokeOnCompletion method
            setValue(dataState)
        }
    }

    abstract fun setJob(job: Job)

    fun asLiveData() = result as LiveData<DataState<ViewStateType>>

    abstract suspend fun createCacheRequestAndReturn()

    abstract suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<ResponseObject>)

    abstract fun createCall(): LiveData<GenericApiResponse<ResponseObject>>

    abstract fun loadFromCache(): CacheObject?


}