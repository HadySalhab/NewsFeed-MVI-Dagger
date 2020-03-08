package com.android.myapplication.newsfeed.ui.headlines.viewmodel

import com.android.myapplication.newsfeed.models.Article
import com.android.myapplication.newsfeed.ui.headlines.state.HeadlinesViewState

/*
* viewState properties setters
* */
fun HeadlinesViewModel.updateViewState(operation:(HeadlinesViewState.HeadlineFields)->Unit){
    val update = getCurrentViewStateOrNew()
    operation(update.headlinesFields)
    setViewState(update)
}
