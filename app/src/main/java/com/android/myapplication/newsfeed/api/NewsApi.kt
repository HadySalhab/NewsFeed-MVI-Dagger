package com.android.myapplication.newsfeed.api

import androidx.lifecycle.LiveData
import com.android.myapplication.newsfeed.BuildConfig
import com.android.myapplication.newsfeed.api.responses.HeadlinesResponse
import com.android.myapplication.newsfeed.util.GenericApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
    @GET("v2/top-headlines")
    fun getTopHeadlines(
        @Query("country") country:String?="",
        @Query("category") category: String = "general",
        @Query("page") page:Int = 1,
        @Query("apiKey") apiKey:String = BuildConfig.API_KEY
    ) : LiveData<GenericApiResponse<HeadlinesResponse>>

}
