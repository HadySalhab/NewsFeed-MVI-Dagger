package com.android.myapplication.newsfeed.ui.headlines.state

import com.android.myapplication.newsfeed.models.Article

data class HeadlinesViewState(
 var headlinesFields : HeadlineFields = HeadlineFields()
){
    data class HeadlineFields(
        var headlinesList: List<Article> = ArrayList<Article>(),
        var searchQuery:String = ""
    )


}