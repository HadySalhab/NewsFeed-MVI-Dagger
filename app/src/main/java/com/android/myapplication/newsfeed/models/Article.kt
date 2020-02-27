package com.android.myapplication.newsfeed.models

data class Article(

    val id:Long,

    var title:String?=null,

    var description:String?=null,

    var url:String?=null,

    var urlToImage:String?=null,

    var publishDate:String?=null,

    var content:String?=null,

    var source:Source

)