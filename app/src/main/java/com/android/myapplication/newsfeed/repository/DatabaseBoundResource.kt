package com.android.myapplication.newsfeed.repository

import com.android.myapplication.newsfeed.util.TESTING_CACHE_DELAY
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class DatabaseBoundResource<ResponseObject, CacheObject, ViewStateType>:BoundResource<ViewStateType>(){
    init {
        doCacheRequest()
    }
    private fun doCacheRequest() {
        coroutineScope.launch {
            delay(TESTING_CACHE_DELAY)
            // View data from cache only and return
            insertOrRemoveArticle()
        }
    }
    abstract suspend fun insertOrRemoveArticle()

}