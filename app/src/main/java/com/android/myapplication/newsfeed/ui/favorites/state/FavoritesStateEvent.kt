package com.android.myapplication.newsfeed.ui.headlines.state

import com.android.myapplication.newsfeed.models.Article

sealed class FavoritesStateEvent {
    class GetFavoritesEvent(
    ) : FavoritesStateEvent()

    class None : FavoritesStateEvent()

    class DeleteFromFavEvent(
        val article: Article
    ) : FavoritesStateEvent()
}
