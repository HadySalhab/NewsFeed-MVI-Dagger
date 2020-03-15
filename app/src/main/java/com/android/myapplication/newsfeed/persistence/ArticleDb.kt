package com.android.myapplication.newsfeed.persistence

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.android.myapplication.newsfeed.models.Source

@Entity(tableName = "article")
data class ArticleDb(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String? = null,
    val author: String? = null,
    val description: String? = null,
    val url: String="",
    @ColumnInfo(name = "url_image")
    val urlToImage: String? = null,
    @ColumnInfo(name = "publish_date")
    val publishDate: String? = null,
    val content: String? = null,
    @Embedded
    val source: Source? = null,
    var isFavorite: Boolean = false
)