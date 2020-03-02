package com.android.myapplication.newsfeed.ui.sources.state

import com.android.myapplication.newsfeed.models.Source

data class SourcesViewState(
    var sourcesField: SourcesField = SourcesField()
) {
    data class SourcesField(
        var sourceList: List<Source> = ArrayList<Source>(),
        var errorScreenMsg: String = ""
    )
}