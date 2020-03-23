package com.android.myapplication.newsfeed.di.main

import androidx.lifecycle.ViewModel
import com.android.myapplication.newsfeed.di.ViewModelKey
import com.android.myapplication.newsfeed.ui.favorites.FavoritesViewModel
import com.android.myapplication.newsfeed.ui.headlines.viewmodel.HeadlinesViewModel
import com.android.myapplication.newsfeed.ui.sources.viewmodel.SourcesViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainViewModelModule {


    @Binds
    @IntoMap
    @ViewModelKey(FavoritesViewModel::class)
    abstract fun bindFavoritesViewModel(favoritesViewModel: FavoritesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HeadlinesViewModel::class)
    abstract fun bindHeadlinesViewModel(headlinesViewModel: HeadlinesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SourcesViewModel::class)
    abstract fun bindSourcesViewModel(sourcesViewModel: SourcesViewModel): ViewModel
}








