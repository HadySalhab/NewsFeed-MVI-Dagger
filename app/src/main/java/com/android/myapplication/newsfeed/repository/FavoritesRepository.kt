package com.android.myapplication.newsfeed.repository

import com.android.myapplication.newsfeed.api.NewsApi
import com.android.myapplication.newsfeed.persistence.ArticlesDao

class FavoritesRepository
constructor(
    val newsApi: NewsApi,
    val articlesDao: ArticlesDao
)