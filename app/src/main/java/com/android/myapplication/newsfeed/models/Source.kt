package com.android.myapplication.newsfeed.models

import androidx.room.ColumnInfo
import androidx.room.Ignore

data class Source(
    @ColumnInfo(name="source_id")
    var id:String="",
    @ColumnInfo(name = "source_name")
    var name:String="",
    @Ignore
    var description:String?=null,
    @Ignore
    var url:String?=null,
    @Ignore
    var country:String?=null,
    @Ignore
    var language:String?=null,
    @Ignore
    var category: String?=null
)