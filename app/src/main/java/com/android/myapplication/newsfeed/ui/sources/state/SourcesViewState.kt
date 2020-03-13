package com.android.myapplication.newsfeed.ui.sources.state

import com.android.myapplication.newsfeed.models.Source

 class SourcesViewState(
    val sourcesField: SourcesField = SourcesField()
) {
     class SourcesField(
        var sourceList: List<Source> = ArrayList<Source>(),
        var errorScreenMsg: String = ""
    )
}