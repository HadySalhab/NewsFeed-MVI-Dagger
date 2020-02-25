package com.android.myapplication.newsfeed.persistence

import androidx.room.Database
import com.android.myapplication.newsfeed.models.Article

@Database(entities = [Article::class],version = 1,exportSchema = false)
abstract class AppDatabase {
    abstract fun getArticlesDao():ArticlesDao
    companion object{
        val DATABASE_NAME: String = "app_db"
    }
}