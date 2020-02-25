package com.android.myapplication.newsfeed.di

import androidx.annotation.MainThread
import com.android.myapplication.newsfeed.di.main.MainFragmentBuildersModule
import com.android.myapplication.newsfeed.di.main.MainModule
import com.android.myapplication.newsfeed.di.main.MainScope
import com.android.myapplication.newsfeed.di.main.MainViewModelModule
import com.android.myapplication.newsfeed.ui.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {

    @MainScope
    @ContributesAndroidInjector(
        modules = [MainModule::class, MainFragmentBuildersModule::class, MainViewModelModule::class]
    )
    abstract fun contributeMainActivity(): MainActivity

}