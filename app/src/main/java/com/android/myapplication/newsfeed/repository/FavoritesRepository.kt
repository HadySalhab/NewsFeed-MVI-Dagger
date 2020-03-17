package com.android.myapplication.newsfeed.repository

import androidx.lifecycle.LiveData
import com.android.myapplication.newsfeed.di.main.MainScope
import com.android.myapplication.newsfeed.models.Article
import com.android.myapplication.newsfeed.persistence.ArticleDb
import com.android.myapplication.newsfeed.persistence.ArticlesDao
import com.android.myapplication.newsfeed.ui.DataState
import com.android.myapplication.newsfeed.ui.headlines.state.FavoritesViewState
import com.android.myapplication.newsfeed.util.convertArticleDBtoUI
import com.android.myapplication.newsfeed.util.convertArticleUItoDB
import kotlinx.coroutines.Job
import javax.inject.Inject

@MainScope
class FavoritesRepository
@Inject
constructor(
    val articlesDao: ArticlesDao
):JobManager("FavoritesRepository") {
    private val TAG: String = "AppDebug"

   fun getFavorites(): LiveData<DataState<FavoritesViewState>>  {
       return object :DatabaseBoundResource<List<ArticleDb>,FavoritesViewState>(){
           override suspend fun dbOperation() {
               val favorites = articlesDao.getAllArticles()
               if(favorites.isNullOrEmpty()){
                   onCompleteJob(DataState.data(FavoritesViewState(FavoritesViewState.FavoritesFields(favoritesList = emptyList(),emptyFavoritesScreen = "You Have No Favorite Article"))))
               }else{
                   onCompleteJob(DataState.data(FavoritesViewState(FavoritesViewState.FavoritesFields(favoritesList = favorites.map { articleDb -> convertArticleDBtoUI(articleDb) }))))
               }
           }
           override fun setJob(job: Job) = addJob("getFavorites", job)
       }.asLiveData()
   }
    fun deleteArticle(article: Article): LiveData<DataState<FavoritesViewState>>  {
        return object :DatabaseBoundResource<List<ArticleDb>,FavoritesViewState>(){
            override suspend fun dbOperation() {
              articlesDao.deleteArticle(convertArticleUItoDB(article).url)
                val favorites = articlesDao.getAllArticles()
                if(favorites.isNullOrEmpty()){
                    onCompleteJob(DataState.data(FavoritesViewState(FavoritesViewState.FavoritesFields(favoritesList = emptyList(),emptyFavoritesScreen = "You Have No Favorite Article"))))
                }else{
                    onCompleteJob(DataState.data(FavoritesViewState(FavoritesViewState.FavoritesFields(favoritesList = favorites.map { articleDb -> convertArticleDBtoUI(articleDb) }))))
                }
            }
            override fun setJob(job: Job) = addJob("deleteArticle", job)
        }.asLiveData()
    }

//
//
//    articlesDao.getArticlesLiveData().switchMap { list: List<ArticleDb> ->
//            if (list.isNullOrEmpty()) {
//                object : LiveData<DataState<FavoritesViewState>>() {
//                    override fun onActive() {
//                        super.onActive()
//                        DataState.data(
//                            FavoritesViewState(
//                                FavoritesViewState.FavoritesFields(
//                                    emptyFavoritesScreen = "You Have No Favorite Articles"
//                                )
//                            )
//                        )
//                    }
//                }
//            } else {
//                object : LiveData<DataState<FavoritesViewState>>() {
//                    override fun onActive() {
//                        super.onActive()
//                        DataState.data(FavoritesViewState(FavoritesViewState.FavoritesFields(
//                            list.map { articleDb -> convertArticleDBtoUI(articleDb) })))
//                    }
//                }
//            }
//        }
}

