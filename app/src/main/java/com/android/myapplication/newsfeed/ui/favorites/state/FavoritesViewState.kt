package com.android.myapplication.newsfeed.ui.headlines.state

import com.android.myapplication.newsfeed.models.Article

data class FavoritesViewState(
    val favoritesFields: FavoritesFields = FavoritesFields()
) {
     class FavoritesFields(
         var favoritesList: List<Article> = ArrayList<Article>(),
         var emptyFavoritesScreen: String = ""
    )
}