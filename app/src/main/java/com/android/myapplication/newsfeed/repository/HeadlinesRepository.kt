package com.android.myapplication.newsfeed.repository

import com.android.myapplication.newsfeed.api.NewsApi
import javax.inject.Inject

class HeadlinesRepository
@Inject
constructor(
    val newsApi: NewsApi
): JobManager("HeadlinesRepository") {
    private val TAG:String = "AppDebug"
}