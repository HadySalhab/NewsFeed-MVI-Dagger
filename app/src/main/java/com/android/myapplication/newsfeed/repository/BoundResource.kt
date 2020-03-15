package com.android.myapplication.newsfeed.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.android.myapplication.newsfeed.ui.DataState
import com.android.myapplication.newsfeed.ui.Response
import com.android.myapplication.newsfeed.util.ERROR_CHECK_NETWORK_CONNECTION
import com.android.myapplication.newsfeed.util.ERROR_UNKNOWN
import com.android.myapplication.newsfeed.util.TAG
import com.android.myapplication.newsfeed.util.UNABLE_TO_RESOLVE_HOST
import kotlinx.coroutines.*

abstract class BoundResource<ViewStateType>{
    protected val result = MediatorLiveData<DataState<ViewStateType>>()
    protected lateinit var job: CompletableJob
    protected lateinit var coroutineScope: CoroutineScope

    init {
        setJob(initNewJob())
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

    protected fun onCompleteJob(dataState: DataState<ViewStateType>) {
        GlobalScope.launch(Dispatchers.Main) {
            job.complete() //this will invoke the invokeOnCompletion method
            setValue(dataState)
        }
    }
    abstract fun setJob(job: Job)
    fun asLiveData() = result as LiveData<DataState<ViewStateType>>
    protected fun setValue(dataState: DataState<ViewStateType>) {
        result.value = dataState
    }
}