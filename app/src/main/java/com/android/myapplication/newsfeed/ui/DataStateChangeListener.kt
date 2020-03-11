package com.android.myapplication.newsfeed.ui

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService

interface DataStateChangeListener {
    fun expandAppBar()
    fun onDataStateChange(dataState: DataState<*>?)
    fun hideSoftKeyboard()
}