package com.android.myapplication.newsfeed.di

import androidx.lifecycle.ViewModelProvider
import com.android.myapplication.newsfeed.viewmodels.ViewModelProviderFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelFactoryModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelProviderFactory): ViewModelProvider.Factory
}