package com.android.myapplication.newsfeed.ui.headlines.viewmodel

import com.android.myapplication.newsfeed.models.Article

fun HeadlinesViewModel.getPage():Int{
    getCurrentViewStateOrNew()?.let {
        return it.headlinesFields.page
    }
}

fun HeadlinesViewModel.getIsQueryExhausted():Boolean{
    getCurrentViewStateOrNew().let {
        return it.headlinesFields.isQueryExhausted
    }
}

fun HeadlinesViewModel.getIsQueryInProgress():Boolean{
    getCurrentViewStateOrNew()?.let {
        return it.headlinesFields.isQueryInProgress
    }

}
fun HeadlinesViewModel.getCountry():String{
    getCurrentViewStateOrNew()?.let {
        return it.headlinesFields.country
    }
}
fun HeadlinesViewModel.getCategory():String{
    getCurrentViewStateOrNew()?.let {
        return it.headlinesFields.category
    }
}

fun HeadlinesViewModel.getHeadlinesList():List<Article>{
    getCurrentViewStateOrNew()?.let {
        return it.headlinesFields.headlinesList
    }
}