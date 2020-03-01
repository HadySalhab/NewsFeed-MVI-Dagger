package com.android.myapplication.newsfeed.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.android.myapplication.newsfeed.models.Article

@Dao
interface ArticlesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(articleDb: ArticleDb): Long //denotes the row # that was inserted in the db

    @Query("SELECT * FROM article")
    fun getAllArticles():List<ArticleDb>
}