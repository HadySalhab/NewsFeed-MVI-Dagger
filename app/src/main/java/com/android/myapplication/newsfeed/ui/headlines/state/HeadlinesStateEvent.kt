package com.android.myapplication.newsfeed.ui.headlines.state

sealed class HeadlinesStateEvent {
    data class HeadlinesSearchEvent(
        var country:String = "",
        var category:String = "general"
    ): HeadlinesStateEvent()
    class None: HeadlinesStateEvent()
}