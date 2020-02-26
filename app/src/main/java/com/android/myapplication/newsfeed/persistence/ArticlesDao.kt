package com.android.myapplication.newsfeed.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.android.myapplication.newsfeed.models.Article

@Dao
interface ArticlesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(article: Article): Long
}