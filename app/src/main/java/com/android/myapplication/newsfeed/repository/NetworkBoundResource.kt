package com.android.myapplication.newsfeed.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.android.myapplication.newsfeed.ui.DataState
import com.android.myapplication.newsfeed.ui.sources.Response
import com.android.myapplication.newsfeed.ui.sources.ResponseType
import com.android.myapplication.newsfeed.util.*
import com.android.myapplication.newsfeed.util.Constants.Companion.NETWORK_TIMEOUT
import com.android.myapplication.newsfeed.util.Constants.Companion.TESTING_CACHE_DELAY
import com.android.myapplication.newsfeed.util.Constants.Companion.TESTING_NETWORK_DELAY
import com.android.myapplication.newsfeed.util.ErrorHandling.Companion.ERROR_CHECK_NETWORK_CONNECTION
import com.android.myapplication.newsfeed.util.ErrorHandling.Companion.ERROR_UNKNOWN
import kotlinx.coroutines.*

//cacheObject: data represented in the db
//responseObject: network response
//viewState

abstract class NetworkBoundResource  <ResponseObject,CacheObject,ViewStateType>
    (
    isNetworkAvailable: Boolean, // is their a network connection?
    isNetworkRequest: Boolean // is this a network request?
){
    private val TAG: String = "AppDebug"
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
                onErrorReturn(
                    ErrorHandling.UNABLE_TODO_OPERATION_WO_INTERNET, shouldUseDialog = true,
                    shouldUseToast = false
                )
            }
        }else{
            doCacheRequest()
        }
    }
    fun doCacheRequest(){
        coroutineScope.launch {
            delay(TESTING_CACHE_DELAY)
            // View data from cache only and return
            createCacheRequestAndReturn()
        }
    }
    fun doNetworkRequest(){
        coroutineScope.launch {

            // simulate a network delay for testing
            delay(TESTING_NETWORK_DELAY)

            withContext(Dispatchers.Main){

                // make network call
                val apiResponse = createCall()
                result.addSource(apiResponse){ response ->
                    result.removeSource(apiResponse)

                    coroutineScope.launch {
                        handleNetworkCall(response)
                    }
                }
            }
        }

        GlobalScope.launch(Dispatchers.IO){
            delay(NETWORK_TIMEOUT)

            if(!job.isCompleted){
                Log.e(TAG, "NetworkBoundResource: JOB NETWORK TIMEOUT." )
                job.cancel(CancellationException(ErrorHandling.UNABLE_TO_RESOLVE_HOST))
            }
        }
    }
    suspend fun handleNetworkCall(response: GenericApiResponse<ResponseObject>){

        when(response){
            is ApiSuccessResponse ->{
                handleApiSuccessResponse(response)
            }
            is ApiErrorResponse ->{
                Log.e(TAG, "NetworkBoundResource: ${response.errorMessage}")
                onErrorReturn(response.errorMessage, true, false)
            }
            is ApiEmptyResponse ->{
                Log.e(TAG, "NetworkBoundResource: Request returned NOTHING (HTTP 204).")
                onErrorReturn("HTTP 204. Returned NOTHING.", true, false)
            }
        }
    }

    fun setValue(dataState: DataState<ViewStateType>){
        result.value = dataState
    }

    @UseExperimental(InternalCoroutinesApi::class)
    private fun initNewJob(): Job {
        Log.d(TAG, "initNewJob: called.")
        job = Job() // create new job
        job.invokeOnCompletion(onCancelling = true, invokeImmediately = true, handler = object: CompletionHandler{
            override fun invoke(cause: Throwable?) {
                if(job.isCancelled){ //the only way the job is going to cancel is if we reached network timeout
                    Log.e(TAG, "NetworkBoundResource: Job has been cancelled.")
                    cause?.let{
                        onErrorReturn(it.message, false, true) //message = ErrorHandling.UNABLE_TO_RESOLVE_HOST
                    }?: onErrorReturn(ERROR_UNKNOWN, false, true)
                }
                else if(job.isCompleted){
                    Log.e(TAG, "NetworkBoundResource: Job has been completed.")
                    // Do nothing? Should be handled already
                }
            }
        })
        coroutineScope = CoroutineScope(Dispatchers.IO + job)
        return job
    }
    //this function will setup the error and complete the job
    fun onErrorReturn(errorMessage: String?, shouldUseDialog: Boolean, shouldUseToast: Boolean){
        var msg = errorMessage
        var useDialog = shouldUseDialog
        var responseType: ResponseType = ResponseType.None()
        if(msg == null){
            msg = ERROR_UNKNOWN
        }
        else if(ErrorHandling.isNetworkError(msg)){ //check if msg = ErrorHandling.UNABLE_TO_RESOLVE_HOST, which is network timeout
            msg = ERROR_CHECK_NETWORK_CONNECTION //maybe the network conx is low
            useDialog = false
        }
        if(shouldUseToast){
            responseType = ResponseType.Toast()
        }
        if(useDialog){
            responseType = ResponseType.Dialog()
        }

        onCompleteJob(DataState.error(Response(msg, responseType)))
    }
    fun onCompleteJob(dataState: DataState<ViewStateType>){
        GlobalScope.launch(Dispatchers.Main) {
            job.complete()
            setValue(dataState)
        }
    }

    abstract fun setJob(job: Job)

    fun asLiveData() = result as LiveData<DataState<ViewStateType>>

    abstract suspend fun createCacheRequestAndReturn()

    abstract suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<ResponseObject>)

    abstract fun createCall(): LiveData<GenericApiResponse<ResponseObject>>

    abstract fun loadFromCache(): LiveData<CacheObject>



}