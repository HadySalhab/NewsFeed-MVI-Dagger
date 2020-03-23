package com.android.myapplication.newsfeed.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import com.android.myapplication.newsfeed.api.NewsApi
import com.android.myapplication.newsfeed.api.data.ArticleNetwork
import com.android.myapplication.newsfeed.api.data.SourceNetwork
import com.android.myapplication.newsfeed.api.responses.HeadlinesResponse
import com.android.myapplication.newsfeed.api.responses.SourcesResponse
import com.android.myapplication.newsfeed.di.main.MainScope
import com.android.myapplication.newsfeed.models.Article
import com.android.myapplication.newsfeed.models.Source
import com.android.myapplication.newsfeed.persistence.ArticleDb
import com.android.myapplication.newsfeed.persistence.ArticlesDao
import com.android.myapplication.newsfeed.ui.DataState
import com.android.myapplication.newsfeed.ui.Response
import com.android.myapplication.newsfeed.ui.sources.state.SourcesViewState
import com.android.myapplication.newsfeed.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import javax.inject.Inject

@MainScope
class SourceRepository
@Inject
constructor(
    val newsApi: NewsApi,
    val articlesDao: ArticlesDao,
    val app: Application
): JobManager("SourceRepository") {
    fun getSources(): LiveData<DataState<SourcesViewState>> {
        return object : NetworkBoundResource<SourcesResponse, Void, SourcesViewState>(
           isNetworkAvailable =  app.isNetworkAvailable()
        ) {
            override fun setJob(job: Job) = addJob("getSources", job)
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<SourcesResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response.body}")
                val sourceList: ArrayList<Source> = ArrayList()
                //handleApiSuccessResponse is already called inside a coroutine with IO dispatcher
                val sourcesNetwork: List<SourceNetwork>? = response.body.sourcesNetwork


                sourcesNetwork?.forEach { sourceNetwork ->
                    sourceList.add(
                        Source(
                            sourceNetwork.id,
                            sourceNetwork.name,
                            sourceNetwork.description,
                            sourceNetwork.url,
                            sourceNetwork.country,
                            sourceNetwork.language,
                            sourceNetwork.category
                        )
                    )
                }

                //switch context because handleApiSuccessResponse is running inside IO dispatcher
                withContext(Dispatchers.Main) {
                    onCompleteJob(
                        DataState.data(
                            data = SourcesViewState(
                                sourcesField = SourcesViewState.SourcesField(sourceList)
                            )
                        )
                    )
                }

            }

            override fun createCall(): LiveData<GenericApiResponse<SourcesResponse>> = newsApi.getSources()
            override suspend fun loadFromCache()= null

        }.asLiveData()
    }
    fun getTopHeadlinesBySource(
        sourceId:String,
        page: Int
    ): LiveData<DataState<SourcesViewState>> {
        return object :
            NetworkBoundResource<HeadlinesResponse, List<ArticleDb>, SourcesViewState>(page,
                app.isNetworkAvailable()
            ) {
            override fun setJob(job: Job) = addJob("getSourceTopHeadlines", job)
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<HeadlinesResponse>) {

                var articleList: ArrayList<Article> = ArrayList()
                val articleDbList: List<ArticleDb>? = loadFromCache()
                val articleNetworkList: List<ArticleNetwork>? = response.body.articlesNetwork
                val isQueryExhausted: Boolean =
                    response.body.totalResults < page * 20
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

                withContext(Dispatchers.Main) {
                    onCompleteJob(
                        DataState.data(
                            SourcesViewState(articlesSourceField =
                            SourcesViewState.ArticlesSourceField(
                                articleList = articleList,
                                isQueryExhausted = isQueryExhausted
                            )
                            )
                        )
                    )
                }


            }

            override fun createCall() =
                newsApi.getTopHeadlinesBySource(sourcesId = sourceId,page = page)


            override suspend fun loadFromCache() = articlesDao.getAllArticles()


        }.asLiveData()
    }

    fun insertArticleToDB(article: Article): LiveData<DataState<SourcesViewState>> {
        return object :
            DatabaseBoundResource<List<ArticleDb>, SourcesViewState>() {
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


    fun deleteArticleFromDB(article: Article): LiveData<DataState<SourcesViewState>> {
        return object :
            DatabaseBoundResource<List<ArticleDb>, SourcesViewState>() {
            override suspend fun dbOperation() {
                withContext(NonCancellable) {
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

    fun checkFavorite(articles: List<Article>, isQueryExhausted:Boolean): LiveData<DataState<SourcesViewState>> {
        return object :
            DatabaseBoundResource<List<ArticleDb>, SourcesViewState>() {
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
                            SourcesViewState(
                                articlesSourceField = SourcesViewState.ArticlesSourceField(
                                    articleList = resetArticles,
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



