package com.android.myapplication.newsfeed.models

import androidx.room.Entity
import com.android.myapplication.newsfeed.repository.SourceRepository
import com.google.gson.annotations.SerializedName

@Entity(tableName = "article")
data class Article(
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
    var source:Source

)