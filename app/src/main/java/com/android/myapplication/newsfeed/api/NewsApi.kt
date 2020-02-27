package com.android.myapplication.newsfeed.api

import android.os.Build
import com.android.myapplication.newsfeed.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
    @GET("v2/top-headlines")
    fun getTopHeadlines(
        @Query("country") country:String,
        @Query("category") category: String = "general",
        @Query("page") page:Int = 1,
        @Query("apiKey") apiKey:String = BuildConfig.API_KEY
    )

}
