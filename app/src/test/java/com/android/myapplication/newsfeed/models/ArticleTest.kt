package com.android.myapplication.newsfeed.models

import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ArticleTest{
    private val URL = "article_url"
    private lateinit var SUT: Article
    @Before
    fun setUp() {
        SUT = Article(url=URL)
    }

    @Test
    fun equal_sameUrl_articleShouldBeEqual() {
        val article = Article(url = URL)
        Assert.assertEquals(SUT,article)
    }
    @Test
    fun equal_differentUrl_articleShouldNotBeEqual(){
        val article = SUT.copy(url = "url_article")
        Assert.assertNotEquals(SUT,article)
    }
}
