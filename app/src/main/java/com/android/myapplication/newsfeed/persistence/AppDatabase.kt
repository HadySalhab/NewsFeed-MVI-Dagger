package com.android.myapplication.newsfeed.persistence

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ArticleDb::class],version = 1,exportSchema = false)
abstract class AppDatabase :RoomDatabase() {
    abstract fun getArticlesDao():ArticlesDao
    companion object{
        val DATABASE_NAME: String = "app_db"
    }
}