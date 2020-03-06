package com.android.myapplication.newsfeed.ui.headlines.viewmodel

import android.util.Log
import com.android.myapplication.newsfeed.ui.headlines.state.HeadlinesStateEvent
import com.android.myapplication.newsfeed.ui.headlines.state.HeadlinesViewState


//country and category can be passed at runtime
fun HeadlinesViewModel.loadFirstPage(country: String, category: String, query: String) {
    Log.d(TAG, "loadFirstPage: ${country}")
    setQueryInProgress(true)
    setQueryExhausted(false) //query is not exhausted for the first attempt
    setQuery(query)
    //later we will move it to the shared preferences
    setCountry(country)
    setCategory(category)
    setPage(1)
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
    if (!getIsQueryExhausted() && !getIsQueryInProgress()) {
        setPage(getPage() + 1) //update the page field in the viewState
        setQueryInProgress(true) //update queryInProgress field in the viewState

        //fire the event using the HeadlinesViewState fields,page increment is already updated
        setStateEvent(
            HeadlinesStateEvent.HeadlinesSearchEvent(
                getCountry(),
                getCategory(),
                getQuery(),
                getPage()
            )
        )

    }
}

fun HeadlinesViewModel.handlePaginationSuccessResult(networkVS: HeadlinesViewState) {
    setQueryExhausted(networkVS.headlinesFields.isQueryExhausted)
    //if the first page, replace all list item, else append to the list item
    if (getPage() == 1) {
        setHeadlineListData(networkVS.headlinesFields.headlinesList)
    } else {
        setHeadlineListData(getHeadlinesList() + networkVS.headlinesFields.headlinesList)
    }

}