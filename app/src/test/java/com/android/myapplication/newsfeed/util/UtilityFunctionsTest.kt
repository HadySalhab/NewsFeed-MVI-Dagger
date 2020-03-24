package com.android.myapplication.newsfeed.util

import com.android.myapplication.newsfeed.models.Article
import com.android.myapplication.newsfeed.persistence.ArticleDb
import org.junit.Assert
import org.junit.Test

class UtilityFunctionsTest {

    @Test
    fun formatStringDate_shouldReturnYearMonthDayFormat() {
        val date = "2020-03-23T18:52:00Z"

        val result = date.formatStringDate()

        Assert.assertEquals("2020-03-23", result)
    }

    @Test
    fun convertArticleUItoDB_shouldReturnDBArticleWithEqualProperties() {
        val article = Article()
        val articleDb = ArticleDb(
            title = article.title,
            author = article.author,
            description = article.description,
            url = article.url,
            urlToImage = article.urlToImage,
            publishDate = article.publishDate,
            content = article.content,
            source = article.source,
            isFavorite = article.isFavorite
        )
        val result = convertArticleUItoDB(article)

        Assert.assertEquals(articleDb, result)
    }
    @Test
    fun convertArticleDBtoUI_shouldReturnUIArticleWithEqualProperties() {
        val articleDB = ArticleDb()
        val article = Article(
            title = articleDB.title,
            author = articleDB.author,
            description = articleDB.description,
            url = articleDB.url,
            urlToImage = articleDB.urlToImage,
            publishDate = articleDB.publishDate,
            content = articleDB.content,
            source = articleDB.source,
            isFavorite = articleDB.isFavorite
        )
        val result = convertArticleDBtoUI(articleDB)

        Assert.assertEquals(article, result)
    }
    @Test
    fun findCommonAndReplace_shouldReturnANewListWithCommonElementUpdated(){
        val url = "URL_ARTICLE"
        val article1 = Article(url=url,isFavorite = true)
        val article1Common = (Article(url=url,isFavorite = false))
        val article2 = Article()
        val article3 = Article()
        val listA = listOf(article2,article1,article3)
        val expected = listOf(article2,article1Common,article3)

        val result  = listA.findCommonAndReplace(article1Common)

        Assert.assertEquals(expected,result)

    }
    @Test
    fun isNetworkError_msgContainsUNABLE_TO_RESOLVE_HOST_shouldReturnTrue(){
        val msg = "Hello $UNABLE_TO_RESOLVE_HOST"
        val result = isNetworkError(msg)
        Assert.assertTrue(result)
    }
    @Test
    fun isNetworkError_msgDoesNotContainsUNABLE_TO_RESOLVE_HOST_shouldReturnFalse(){
        val msg = "Hello World"
        val result = isNetworkError(msg)
        Assert.assertFalse(result)
    }
}