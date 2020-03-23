package com.android.myapplication.newsfeed.api.data

import com.google.gson.annotations.SerializedName

data class SourceNetwork(
    @SerializedName("id")
    var id:String,
    @SerializedName("name")
    var name:String,
    @SerializedName("description")
    var description:String,
    @SerializedName("url")
    var url:String,
    @SerializedName("country")
    var country:String,
    @SerializedName("language")
    var language:String,
    @SerializedName("category")
    var category: String
)