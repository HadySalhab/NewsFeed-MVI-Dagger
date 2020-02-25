package com.android.myapplication.newsfeed.di.main

import com.android.myapplication.newsfeed.ui.favorites.FavoritesFragment
import com.android.myapplication.newsfeed.ui.headlines.HeadlineFragment
import com.android.myapplication.newsfeed.ui.sources.ArticlesSourceFragment
import com.android.myapplication.newsfeed.ui.sources.SourcesFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainFragmentBuildersModule {



    @ContributesAndroidInjector
    abstract fun contributeFavoritesFragment():FavoritesFragment

    @ContributesAndroidInjector
    abstract fun contributeHeadlineFragment():HeadlineFragment

    @ContributesAndroidInjector
    abstract fun contributeSourcesFragment():SourcesFragment

    @ContributesAndroidInjector
    abstract fun contributeArticlesSourceFragment():ArticlesSourceFragment


}