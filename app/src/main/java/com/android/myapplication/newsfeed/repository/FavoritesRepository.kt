package com.android.myapplication.newsfeed.repository

import com.android.myapplication.newsfeed.api.NewsApi
import com.android.myapplication.newsfeed.di.main.MainScope
import com.android.myapplication.newsfeed.persistence.ArticlesDao
import javax.inject.Inject

@MainScope
class FavoritesRepository
@Inject
constructor(
    val newsApi: NewsApi,
    val articlesDao: ArticlesDao
)