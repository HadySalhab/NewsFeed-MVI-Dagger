package com.android.myapplication.newsfeed.ui.sources.state
sealed class SourcesStateEvent {
    data class SourcesSearchEvent(
        var country:String = "",
        var category:String = "general"
    ): SourcesStateEvent()
    class None: SourcesStateEvent()
}