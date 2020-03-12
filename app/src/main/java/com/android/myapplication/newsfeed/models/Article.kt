package com.android.myapplication.newsfeed.models

data class Article(

    val id: Long = 0,

    val title: String?=null,

    val author: String?=null,

    val description: String?=null,

    val url: String?=null,

    val urlToImage: String?=null,

    val publishDate: String?=null,

    val content: String?=null,

    val source: Source?=null,

    var isFavorite: Boolean = false

)