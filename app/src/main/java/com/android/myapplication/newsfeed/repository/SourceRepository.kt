package com.android.myapplication.newsfeed.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import com.android.myapplication.newsfeed.api.NewsApi
import com.android.myapplication.newsfeed.api.data.SourceNetwork
import com.android.myapplication.newsfeed.api.responses.SourcesResponse
import com.android.myapplication.newsfeed.di.main.MainScope
import com.android.myapplication.newsfeed.models.Source
import com.android.myapplication.newsfeed.ui.DataState
import com.android.myapplication.newsfeed.ui.sources.state.SourcesViewState
import com.android.myapplication.newsfeed.util.ApiSuccessResponse
import com.android.myapplication.newsfeed.util.GenericApiResponse
import com.android.myapplication.newsfeed.util.TAG
import com.android.myapplication.newsfeed.util.isNetworkAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import javax.inject.Inject

@MainScope
class SourceRepository
@Inject
constructor(
    val newsApi: NewsApi,
    val app: Application
): JobManager("SourceRepository") {
    fun getSources(): LiveData<DataState<SourcesViewState>> {
        Log.d(TAG, "SourceRepository: getSources() is called ")
        return object : NetworkBoundResource<SourcesResponse, Void, SourcesViewState>(
           isNetworkAvailable =  app.isNetworkAvailable()
        ) {
            override fun setJob(job: Job) = addJob("getSources", job)



            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<SourcesResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response.body}")
                val sourceList: ArrayList<Source> = ArrayList()
                //handleApiSuccessResponse is already called inside a coroutine with IO dispatcher
                val sourcesNetwork: List<SourceNetwork>? = response.body.sourcesNetwork


                sourcesNetwork?.forEach { sourceNetwork ->
                    sourceList.add(
                        Source(
                            sourceNetwork.id,
                            sourceNetwork.name,
                            sourceNetwork.description,
                            sourceNetwork.url,
                            sourceNetwork.country,
                            sourceNetwork.language,
                            sourceNetwork.category
                        )
                    )
                }

                //switch context because handleApiSuccessResponse is running inside IO dispatcher
                withContext(Dispatchers.Main) {
                    onCompleteJob(
                        DataState.data(
                            data = SourcesViewState(
                                sourcesField = SourcesViewState.SourcesField(sourceList)
                            )
                        )
                    )
                }

            }

            override fun createCall(): LiveData<GenericApiResponse<SourcesResponse>> = newsApi.getSources()
            override suspend fun loadFromCache()= null

        }.asLiveData()
    }
}