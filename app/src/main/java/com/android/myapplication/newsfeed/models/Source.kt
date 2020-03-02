package com.android.myapplication.newsfeed.models

import com.google.gson.annotations.SerializedName

data class Source(
    var id:String?=null,
    var name:String?=null,
    var description:String?=null,
    var url:String?=null,
    var country:String?=null,
    var language:String?=null,
    var category: String?=null
)