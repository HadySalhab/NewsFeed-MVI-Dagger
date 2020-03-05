package com.android.myapplication.newsfeed.ui.headlines.viewmodel

import com.android.myapplication.newsfeed.models.Article

fun HeadlinesViewModel.setQuery(query: String) {
    val update = getCurrentViewStateOrNew()
    if (query.equals(update.headlinesFields.searchQuery)) {
        return
    }
    update.headlinesFields.searchQuery = query
    setViewState(update)
}
fun HeadlinesViewModel.setErrorScreenMsg(errorScreenMsg:String){
    val update = getCurrentViewStateOrNew()
    if (errorScreenMsg.equals(update.headlinesFields.errorScreenMsg)) {
        return
    }
    update.headlinesFields.errorScreenMsg = errorScreenMsg
    setViewState(update)
}

fun HeadlinesViewModel.setHeadlineListData(headlinesList: List<Article>) {
    val update = getCurrentViewStateOrNew()
    //we are not checking if it's the same list being passed, because RecycleView DiffUtil will take care of it
    update.headlinesFields.headlinesList = headlinesList
    setViewState(update)
}


fun HeadlinesViewModel.setQueryExhausted(isExhausted:Boolean){
    val update = getCurrentViewStateOrNew()
    update.headlinesFields.isQueryExhausted = isExhausted
    setViewState(update)
}

fun HeadlinesViewModel.setQueryInProgress(isInProgress:Boolean){
    val update = getCurrentViewStateOrNew()
    update.headlinesFields.isQueryInProgress = isInProgress
    setViewState(update)
}

fun HeadlinesViewModel.setPage(pageNumber:Int){
    val update = getCurrentViewStateOrNew()
    update.headlinesFields.page = pageNumber
    setViewState(update)
}

fun HeadlinesViewModel.setCountry(country:String){
    val update = getCurrentViewStateOrNew()
    if (country.equals(update.headlinesFields.country)) {
        return
    }
    update.headlinesFields.country = country
    setViewState(update)
}
fun HeadlinesViewModel.setCategory(category:String){
    val update = getCurrentViewStateOrNew()
    if (category.equals(update.headlinesFields.category)) {
        return
    }
    update.headlinesFields.category = category
    setViewState(update)
}