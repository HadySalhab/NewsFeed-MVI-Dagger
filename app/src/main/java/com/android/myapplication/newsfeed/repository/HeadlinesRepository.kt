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
            NetworkBoundResource<HeadlinesResponse, List<ArticleDb>, HeadlinesViewState>(page,
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
                    articleList = ArrayList(articleNetworkList.map { articleNetwork ->
                        articleNetwork.run {

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

                        }
                    })
                }
                if (!articleDbList.isNullOrEmpty()) {
                    val favArticle = articleDbList.map { convertArticleDBtoUI(it) }
                    val commonArticles = favArticle.intersect(articleList)
                    if (!commonArticles.isNullOrEmpty()) {
                        commonArticles.forEach { commonArticle ->
                            articleList =
                                ArrayList(articleList.toList().findCommonAndReplace(commonArticle))
                        }
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
            DatabaseBoundResource<List<ArticleDb>, HeadlinesViewState>() {
            override suspend fun dbOperation() {
                withContext(NonCancellable) {
                    article.isFavorite = true
                    articlesDao.insert(convertArticleUItoDB(article))
                }
                withContext(Dispatchers.Main) {
                    onCompleteJob(
                        DataState.data(
                            null,
                            Response.toastResponse("Article Added To Favorite")
                        )
                    )
                }
            }

            override fun setJob(job: Job) = addJob("addToFavorite", job)

        }.asLiveData()
    }


    fun deleteArticleFromDB(article: Article): LiveData<DataState<HeadlinesViewState>> {
        return object :
            DatabaseBoundResource<List<ArticleDb>, HeadlinesViewState>() {
            override suspend fun dbOperation() {
                withContext(NonCancellable) {
                    Log.d(TAG, "insertOrRemoveArticle: ${article.isFavorite} ")
                    articlesDao.deleteArticle(convertArticleUItoDB(article).url)
                }
                withContext(Dispatchers.Main) {
                    onCompleteJob(
                        DataState.data(
                            null,
                            Response.toastResponse("Article Removed From Favorite")
                        )
                    )
                }
            }

            override fun setJob(job: Job) = addJob("removeFromFavorite", job)

        }.asLiveData()
    }

    fun checkFavorite(articles: List<Article>,isQueryExhausted:Boolean): LiveData<DataState<HeadlinesViewState>> {
        return object :
            DatabaseBoundResource<List<ArticleDb>, HeadlinesViewState>() {
            override suspend fun dbOperation() {
                var resetArticles = articles.map { article -> article.copy(isFavorite = false) }
                val favArticles = articlesDao.getAllArticles()
                if (!favArticles.isNullOrEmpty()) {
                    val favArticle = favArticles.map { convertArticleDBtoUI(it) }
                    val commonArticles = favArticle.intersect(resetArticles)
                    if (!commonArticles.isNullOrEmpty()) {
                        commonArticles.forEach { commonArticle ->
                            resetArticles =
                                ArrayList(resetArticles.toList().findCommonAndReplace(commonArticle))
                        }
                    }
                }
                Log.d(TAG, "dbOperation: ${isQueryExhausted}")
                withContext(Dispatchers.Main) {
                    onCompleteJob(
                        DataState.data(
                            HeadlinesViewState(
                                HeadlinesViewState.HeadlineFields(
                                    headlinesList = resetArticles,
                                    isQueryExhausted = isQueryExhausted
                                )
                            )
                        )
                    )
                }
            }

            override fun setJob(job: Job) = addJob("checkFavorite", job)

        }.asLiveData()
    }

}
