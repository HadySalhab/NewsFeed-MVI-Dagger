package com.android.myapplication.newsfeed.ui

interface DataStateChangeListener {
    fun expandAppBar()
    fun onDataStateChange(dataState: DataState<*>?)
    fun hideSoftKeyboard()
}