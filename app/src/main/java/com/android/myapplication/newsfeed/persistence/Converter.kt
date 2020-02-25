package com.android.myapplication.newsfeed.persistence

import androidx.room.TypeConverter
import com.android.myapplication.newsfeed.models.Source
import com.google.gson.Gson

class Converter {
        @TypeConverter
        fun toSource(sourceString: String?): Source? {
            if (sourceString == null) {
                return null
            }
            val gson = Gson()
            return gson.fromJson(sourceString, Source::class.java)
        }

        @TypeConverter
        fun toSourceString(source: Source?): String? {
            if(source==null){
                return null
            }
            val gson = Gson()
            return gson.toJson(source,String::class.java)

        }


}