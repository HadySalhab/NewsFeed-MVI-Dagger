package com.android.myapplication.newsfeed.util

fun String.formatStringDate():String?{
    return this.removeRange(this.indexOf("T") until this.length)
}