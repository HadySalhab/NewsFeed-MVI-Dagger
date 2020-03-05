package com.android.myapplication.newsfeed.ui.headlines.state

import com.android.myapplication.newsfeed.models.Article

data class HeadlinesViewState(
    var headlinesFields: HeadlineFields = HeadlineFields()
) {
    data class HeadlineFields(

        //these fields will be updated from the viewState coming from the network
        var headlinesList: List<Article> = ArrayList<Article>(),
        var errorScreenMsg: String = "",
        var isQueryExhausted:Boolean = false,  //true when no more result from the api , false by default

        //country and category are needed to update the overflow menu items from shared preferences
        //and needed to query the next page for pagination
        //these fields are updated from the UI layer
        var country:String = "us",  //country of the viewState is us by default
        var category:String = "general", //category of the viewState is general by default
        var searchQuery: String = "",
        var page:Int = 1, //page = 1 by default
        var isQueryInProgress:Boolean = false // same if it's loading , false by default


    )


}