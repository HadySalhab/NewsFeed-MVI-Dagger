package com.android.myapplication.newsfeed.ui.headlines.viewmodel

import com.android.myapplication.newsfeed.models.Article
import com.android.myapplication.newsfeed.ui.headlines.state.HeadlinesViewState

fun HeadlinesViewModel.getVSHeadlines():HeadlinesViewState.HeadlineFields{
   return getCurrentViewStateOrNew().let{ viewState->
         viewState.headlinesFields
    }
}