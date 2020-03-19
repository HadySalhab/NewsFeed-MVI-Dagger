package com.android.myapplication.newsfeed.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.android.myapplication.newsfeed.ui.DataState
import com.android.myapplication.newsfeed.ui.Response
import com.android.myapplication.newsfeed.util.*
import kotlinx.coroutines.*



//cacheObject: data represented in the db
//responseObject: network response
//viewState
abstract class NetworkBoundResource<ResponseObject, CacheObject, ViewStateType>
    (page:Int=1,
    isNetworkAvailable: Boolean // is their a network connection?
):BoundResource<ViewStateType> (){


  init {
      setValue(DataState.loading(isLoading = true))
      if (isNetworkAvailable) {
          doNetworkRequest()
      } else {
          if(page>1){
              onCompleteJob(DataState.error(Response.dialogResponse(
                  UNABLE_TO_LOAD_MORE_PAGE_WO_INTERNET)))
          }else{
              onCompleteJob(DataState.error(Response.dialogResponse(UNABLE_TODO_OPERATION_WO_INTERNET)))
          }

      }
  }

    private fun doNetworkRequest() {
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
    private suspend fun handleNetworkCall(response: GenericApiResponse<ResponseObject>) {

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



    abstract suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<ResponseObject>)

    abstract fun createCall(): LiveData<GenericApiResponse<ResponseObject>>

    suspend abstract fun loadFromCache(): CacheObject?
}
