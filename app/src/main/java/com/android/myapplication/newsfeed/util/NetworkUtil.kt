package com.android.myapplication.newsfeed.util

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.android.myapplication.newsfeed.di.main.MainScope
import javax.inject.Inject

@MainScope
class NetworkUtil
@Inject
constructor(val app:Application){
    private val TAG = "AppDebug"
    fun isConnectedToTheInternet(): Boolean{
        val cm = app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        try{
            return cm.activeNetworkInfo.isConnected
        }catch (e: Exception){
            Log.e(TAG, "isConnectedToTheInternet: ${e.message}")
        }
        return false
    }
}