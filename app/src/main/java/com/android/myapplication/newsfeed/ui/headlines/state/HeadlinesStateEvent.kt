package com.android.myapplication.newsfeed.ui.headlines.state

sealed class HeadlinesStateEvent {
     class HeadlinesSearchEvent(
        val country:String ,
        val category:String,
        val searchQuery:String,
        val page:Int
    ): HeadlinesStateEvent()
    class None: HeadlinesStateEvent()
}