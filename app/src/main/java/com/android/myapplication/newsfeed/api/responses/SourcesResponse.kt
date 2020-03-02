package com.android.myapplication.newsfeed.api.responses

import com.android.myapplication.newsfeed.api.data.SourceNetwork
import com.android.myapplication.newsfeed.models.Source
import com.google.gson.annotations.SerializedName

class SourcesResponse (
    @SerializedName("sources")
    val sourcesNetwork:List<SourceNetwork>?=null
)