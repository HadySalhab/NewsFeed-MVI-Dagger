package com.android.myapplication.newsfeed.ui.headlines.state

sealed class HeadlinesStateEvent {
    class HeadlinesSearchEvent: HeadlinesStateEvent()
    class None: HeadlinesStateEvent()
}