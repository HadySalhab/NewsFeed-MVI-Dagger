package com.android.myapplication.newsfeed.ui.headlines.viewmodel

import android.util.Log
import com.android.myapplication.newsfeed.ui.headlines.state.HeadlinesStateEvent
import com.android.myapplication.newsfeed.ui.headlines.state.HeadlinesViewState
import com.android.myapplication.newsfeed.util.TAG
import com.android.myapplication.newsfeed.util.findCommonAndReplace

const val EMPTY_STRING = ""
//country and category can be passed at runtime
fun HeadlinesViewModel.loadFirstPage(country: String=EMPTY_STRING, category: String=EMPTY_STRING, query: String=EMPTY_STRING) {
    Log.d(TAG, "loadFirstPage: ${country}")
    updateViewState { headlinesFields->
        with(headlinesFields){
            isQueryInProgress = true
            isQueryExhausted = false
            searchQuery = query
            page = 1
        }
    }
    setStateEvent(
        HeadlinesStateEvent.HeadlinesSearchEvent(
            country,
            category,
            query,
            1
        )
    ) //fire the event
}

//country & category are already known
fun HeadlinesViewModel.loadNextPage() {
    // we should not get the next page when the query is exhausted or when the query is still in progress
    if (!getVSHeadlines().isQueryExhausted && !getVSHeadlines().isQueryInProgress) {
        updateViewState { headlinesFields->
            with(headlinesFields){
                page++
                isQueryInProgress = true
            }
        }
        //fire the event using the HeadlinesViewState fields,page increment is already updated

        with(getVSHeadlines()) {
            setStateEvent(
                HeadlinesStateEvent.HeadlinesSearchEvent(
                    country,
                    category,
                    searchQuery,
                    page
                )
            )
        }
    }
}

fun HeadlinesViewModel.handlePaginationSuccessResult(networkVS: HeadlinesViewState) {
    //if the first page, replace all list item, else append to the list item
    val networkHeadlineFields = networkVS.headlinesFields
    updateViewState { headlinesFields->
        with(headlinesFields){
            isQueryExhausted = networkHeadlineFields.isQueryExhausted
            Log.d(TAG, "handlePaginationSuccessResult: ${isQueryExhausted}")
            if(page==1){
                headlinesList = networkHeadlineFields.headlinesList
            }else{
                val commonArticles = networkHeadlineFields.headlinesList.intersect(headlinesList)
                if (!commonArticles.isNullOrEmpty()) {
                    commonArticles.forEach { commonArticle ->
                        headlinesList = ArrayList(headlinesList.toList().findCommonAndReplace(commonArticle))
                    }
                }else{
                    headlinesList += networkHeadlineFields.headlinesList
                }
            }
        }
    }
}