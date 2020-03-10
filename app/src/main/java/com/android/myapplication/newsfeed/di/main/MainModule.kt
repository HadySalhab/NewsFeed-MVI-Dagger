package com.android.myapplication.newsfeed.di.main

import android.app.Application
import androidx.room.Room
import com.android.myapplication.newsfeed.api.NewsApi
import com.android.myapplication.newsfeed.persistence.AppDatabase
import com.android.myapplication.newsfeed.persistence.ArticlesDao
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class MainModule {

    @MainScope
    @Provides
    fun provideNewsApi(retrofitBuilder: Retrofit.Builder)= retrofitBuilder
            .build()
            .create(NewsApi::class.java)




    @MainScope
    @Provides
    fun provideAppDb(app: Application) = Room
            .databaseBuilder(app, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()


    @MainScope
    @Provides
    fun provideArticlesDao(db: AppDatabase): ArticlesDao {
        return db.getArticlesDao()
    }


//    @MainScope
//    @Provides
//    fun provideHeadlinesRepository(newsApi: NewsApi,articlesDao: ArticlesDao,networkUtil: NetworkUtil): HeadlinesRepository {
//        return HeadlinesRepository(newsApi = newsApi,articlesDao = articlesDao,networkUtil = networkUtil)
//    }

//    @MainScope
//    @Provides
//    fun provideSourceRepository(newsApi: NewsApi,networkUtil: NetworkUtil): SourceRepository {
//        return SourceRepository(newsApi = newsApi,networkUtil = networkUtil)
//    }

//    @MainScope
//    @Provides
//    fun provideFavoritesRepository(newsApi: NewsApi,articlesDao: ArticlesDao): FavoritesRepository {
//        return FavoritesRepository(newsApi = newsApi,articlesDao = articlesDao)
//    }


}

















