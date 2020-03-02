package com.android.myapplication.newsfeed.persistence

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.android.myapplication.newsfeed.models.Source
import com.google.gson.annotations.SerializedName

@Entity(tableName = "article")
data class ArticleDb(
    @PrimaryKey(autoGenerate = true)
    val id:Long,
    var author:String?=null,
    var title:String?=null,
    var description:String?=null,
    var url:String?=null,
    @ColumnInfo(name="url_image")
    var urlToImage:String?=null,
    @ColumnInfo(name="publish_date")
    var publishDate:String?=null,
    var content:String?=null,
    var source: Source?=null
)