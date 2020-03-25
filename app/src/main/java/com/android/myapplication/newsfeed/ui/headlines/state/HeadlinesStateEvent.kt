package com.android.myapplication.newsfeed.ui.headlines.state

import com.android.myapplication.newsfeed.models.Article

sealed class HeadlinesStateEvent {
   data class HeadlinesSearchEvent(
        val country: String,
        val category: String,
        val searchQuery: String,
        val page: Int
    ) : HeadlinesStateEvent()

    class None : HeadlinesStateEvent()

    class HeadlinesAddToFavEvent(
        val article: Article
    ) : HeadlinesStateEvent()

    class HeadlinesCheckFavEvent(val articles:List<Article>,val isQueryExhausted:Boolean):HeadlinesStateEvent()

    class HeadlinesRemoveFromFavEvent(
        val article: Article
    ) : HeadlinesStateEvent()
}
