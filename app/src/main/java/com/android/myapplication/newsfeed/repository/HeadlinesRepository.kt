package com.android.myapplication.newsfeed.repository


import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import com.android.myapplication.newsfeed.api.NewsApi
import com.android.myapplication.newsfeed.api.data.ArticleNetwork
import com.android.myapplication.newsfeed.api.responses.HeadlinesResponse
import com.android.myapplication.newsfeed.di.main.MainScope
import com.android.myapplication.newsfeed.models.Article
import com.android.myapplication.newsfeed.persistence.ArticleDb
import com.android.myapplication.newsfeed.persistence.ArticlesDao
import com.android.myapplication.newsfeed.ui.DataState
import com.android.myapplication.newsfeed.ui.Response
import com.android.myapplication.newsfeed.ui.headlines.state.HeadlinesViewState
import com.android.myapplication.newsfeed.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import javax.inject.Inject

@MainScope
class HeadlinesRepository
@Inject
constructor(
    val newsApi: NewsApi,
    val articlesDao: ArticlesDao,
    val app: Application
) : JobManager("HeadlinesRepository") {
    private val TAG: String = "AppDebug"

    fun getTopHeadlines(
        country: String,
        category: String,
        searchQuery: String,
        page: Int
    ): LiveData<DataState<HeadlinesViewState>> {
        return object :
            NetworkBoundResource<HeadlinesResponse, List<ArticleDb>, HeadlinesViewState>(
                app.isNetworkAvailable()
            ) {
            override fun setJob(job: Job) = addJob("getTopHeadlines", job)
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<HeadlinesResponse>) {

                var articleList: ArrayList<Article> = ArrayList()
                //handleApiSuccessResponse is already called inside a coroutine with IO dispatcher
                val articleDbList: List<ArticleDb>? = loadFromCache()
                Log.d(TAG, "handleApiSuccessResponse: ${response.body.status}")
                val articleNetworkList: List<ArticleNetwork>? = response.body.articlesNetwork
                val isQueryExhausted: Boolean =
                    response.body.totalResults < page * 20 //20 is the default number of articles returned per page
                if (!articleNetworkList.isNullOrEmpty()) {
                    articleNetworkList.forEach { articleNetwork ->
                        articleNetwork.run {
                            articleList.add(
                                Article(
                                    title = title,
                                    description = description,
                                    url = url,
                                    urlToImage = urlToImage,
                                    publishDate = publishDate,
                                    content = content,
                                    source = source,
                                    author = author,
                                    isFavorite = false
                                )
                            )
                        }
                    }
                }
                if(!articleDbList.isNullOrEmpty()){
                    articleDbList.forEach { articleDb->
                        Log.d(TAG, "handleApiSuccessResponse articleDB: $articleDb")
                       val article =  convertArticleDBtoUI(articleDb)
                        article.isFavorite = false
                        articleList= ArrayList(articleList.replaceArticleAndReturn(article))
                    }
                }

                //switch context because handleApiSuccessResponse is running inside IO dispatcher
                withContext(Dispatchers.Main) {
                    onCompleteJob(
                        DataState.data(
                            HeadlinesViewState(
                                HeadlinesViewState.HeadlineFields(
                                    headlinesList = articleList,
                                    isQueryExhausted = isQueryExhausted
                                )
                            )
                        )
                    )
                }


            }

            override fun createCall() =
                newsApi.getTopHeadlines(country, category, page, searchQuery)


            override suspend fun loadFromCache() = articlesDao.getAllArticles()


        }.asLiveData()
    }

    fun insertArticleToDB(article: Article): LiveData<DataState<HeadlinesViewState>> {
        return object :
            DatabaseBoundResource<HeadlinesResponse, List<ArticleDb>, HeadlinesViewState>() {
            override suspend fun insertOrRemoveArticle() {
                withContext(NonCancellable) {
                    article.isFavorite = true
                    articlesDao.insert(convertArticleUItoDB(article))
                }
                onCompleteJob(
                    DataState.data(
                        null,
                        Response.toastResponse("Article Added To Favorite")
                    )
                )
            }

            override fun setJob(job: Job) = addJob("addToFavorite", job)

        }.asLiveData()
    }


     fun deleteArticleFromDB(article: Article): LiveData<DataState<HeadlinesViewState>> {
        return object :
            DatabaseBoundResource<HeadlinesResponse, List<ArticleDb>, HeadlinesViewState>() {
            override suspend fun insertOrRemoveArticle() {
                withContext(NonCancellable) {
                    Log.d(TAG, "insertOrRemoveArticle: ${article.isFavorite} ")
                    articlesDao.deleteArticle(convertArticleUItoDB(article).url)
                }
                onCompleteJob(
                    DataState.data(
                        null,
                        Response.toastResponse("Article Removed From Favorite")
                    )
                )
            }

            override fun setJob(job: Job) = addJob("removeFromFavorite", job)

        }.asLiveData()
    }
}
