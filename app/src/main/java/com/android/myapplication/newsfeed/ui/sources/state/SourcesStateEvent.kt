package com.android.myapplication.newsfeed.ui.sources.state
sealed class SourcesStateEvent {
     class SourcesSearchEvent(
        val country:String = "",
        val category:String = "general"
    ): SourcesStateEvent()
    class None: SourcesStateEvent()
}