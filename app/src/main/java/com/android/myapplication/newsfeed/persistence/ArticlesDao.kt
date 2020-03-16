package com.android.myapplication.newsfeed.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ArticlesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(articleDb: ArticleDb): Long //denotes the row # that was inserted in the db

    @Query("SELECT * FROM article")
    fun getArticlesLiveData(): LiveData<List<ArticleDb>>

    @Query("SELECT * FROM article")
    suspend fun getAllArticles(): List<ArticleDb>?

    @Query("DELETE FROM article WHERE url = :articleUrl")
    suspend fun deleteArticle(articleUrl: String)
}