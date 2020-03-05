package com.android.myapplication.newsfeed.ui.headlines.state

sealed class HeadlinesStateEvent {
    data class HeadlinesSearchEvent(
        var country:String ,
        var category:String,
        var page:Int
    ): HeadlinesStateEvent()
    class None: HeadlinesStateEvent()
}