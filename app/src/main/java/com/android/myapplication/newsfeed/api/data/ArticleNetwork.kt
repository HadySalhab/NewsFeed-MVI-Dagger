package com.android.myapplication.newsfeed.api.data

import com.android.myapplication.newsfeed.models.Source
import com.google.gson.annotations.SerializedName

data class ArticleNetwork(
    @SerializedName("author")
    val author:String,
    @SerializedName("title")
    val title:String,
    @SerializedName("description")
    val description:String,
    @SerializedName("url")
    val url:String,
    @SerializedName("urlToImage")
    val urlToImage:String,
    @SerializedName("publishedAt")
    val publishDate:String,
    @SerializedName("content")
    val content:String,
    @SerializedName("source")
    val source: Source
)