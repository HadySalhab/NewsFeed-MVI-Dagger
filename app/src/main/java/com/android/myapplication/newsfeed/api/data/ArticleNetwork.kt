package com.android.myapplication.newsfeed.api.data

import com.android.myapplication.newsfeed.models.Source
import com.google.gson.annotations.SerializedName

data class ArticleNetwork(
    @SerializedName("author")
    var author:String?=null,
    @SerializedName("title")
    var title:String?=null,
    @SerializedName("description")
    var description:String?=null,
    @SerializedName("url")
    var url:String?=null,
    @SerializedName("urlToImage")
    var urlToImage:String?=null,
    @SerializedName("publishedAt")
    var publishDate:String?=null,
    @SerializedName("content")
    var content:String?=null,
    @SerializedName("source")
    var source: Source?=null
)