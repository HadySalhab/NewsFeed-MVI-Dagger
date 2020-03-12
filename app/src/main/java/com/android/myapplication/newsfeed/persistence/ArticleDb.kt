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
    val author:String,
    val title:String,
    val description:String,
    val url:String,
    @ColumnInfo(name="url_image")
    val urlToImage:String,
    @ColumnInfo(name="publish_date")
    val publishDate:String,
    val content:String,
    val source: Source
)