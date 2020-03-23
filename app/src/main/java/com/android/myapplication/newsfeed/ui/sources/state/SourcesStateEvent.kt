package com.android.myapplication.newsfeed.ui.sources.state

import com.android.myapplication.newsfeed.models.Article

sealed class SourcesStateEvent {
     class SourcesSearchEvent(
        val country:String = "",
        val category:String = "general"
    ): SourcesStateEvent()
    class SourceArticlesEvent(
        val sourceId:String,
        val page: Int
    ):SourcesStateEvent()
    class None: SourcesStateEvent()
    class SourceArticlesAddToFavEvent(
        val article: Article
    ) : SourcesStateEvent()

    class SourceArticlesCheckFavEvent(val articles:List<Article>, val isQueryExhausted:Boolean):
        SourcesStateEvent()

    class SourceArticlesRemoveFromFavEvent(
        val article: Article
    ) : SourcesStateEvent()
}