package com.android.myapplication.newsfeed.util

import android.content.SharedPreferences

class PreferenceUtil{

    companion object{
        // Shared Preference Files:
        const val APP_PREFERENCES: String = "com.android.myapplication.newsfeed.APP_PREFERENCES"
        const val ARTICLE_COUNTRY_KEY= "com.android.myapplication.newsfeed.ARTICLE_COUNTRY"
        const val ARTICLE_CATEGORY_KEY= "com.android.myapplication.newsfeed.ARTICLE_CATEGORY"
    }


}

fun SharedPreferences.getCountry():String{
    return getString(PreferenceUtil.ARTICLE_COUNTRY_KEY,SourcesCategoriesAndCountries.AUSTRALIA)!!
}

fun SharedPreferences.getCategory():String{
    return getString(PreferenceUtil.ARTICLE_CATEGORY_KEY,SourcesCategoriesAndCountries.GENERAL)!!
}

fun SharedPreferences.Editor.saveCountryAndCategory(country:String,category:String){
    putString(PreferenceUtil.ARTICLE_COUNTRY_KEY,country)
    putString(PreferenceUtil.ARTICLE_CATEGORY_KEY,category)
    apply()
}
