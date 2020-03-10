package com.android.myapplication.newsfeed.persistence

import androidx.room.TypeConverter
import com.android.myapplication.newsfeed.models.Source
import com.google.gson.Gson

class Converter {
    @TypeConverter
    fun toSource(sourceString: String?) = if (sourceString == null) {
        null
    } else {
        val gson = Gson()
        gson.fromJson(sourceString, Source::class.java)
    }

    @TypeConverter
    fun toSourceString(source: Source?) = if (source == null) {
        null
    } else {
        val gson = Gson()
        gson.toJson(source, String::class.java)
    }

}