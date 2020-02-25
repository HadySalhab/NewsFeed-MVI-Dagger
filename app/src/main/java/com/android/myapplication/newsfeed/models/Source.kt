package com.android.myapplication.newsfeed.models

import com.google.gson.annotations.SerializedName

data class Source(
    @SerializedName("id")
    var id:String?=null,
    @SerializedName("name")
    var name:String?=null,
    @SerializedName("description")
    var description:String?=null,
    @SerializedName("url")
    var url:String?=null,
    @SerializedName("country")
    var country:String?=null,
    @SerializedName("language")
    var language:String?=null,
    @SerializedName("category")
    var category: String?=null
)