package com.android.myapplication.newsfeed.api.responses

import com.android.myapplication.newsfeed.api.data.ArticleNetwork
import com.android.myapplication.newsfeed.models.Article
import com.google.gson.annotations.SerializedName

class HeadlinesResponse(
    @SerializedName("articles")
    var articleNetwork: ArticleNetwork,
    @SerializedName("status")
    var status: String,
    @SerializedName("code")
    var code: String,
    @SerializedName("message")
    var message: String,
    @SerializedName("totalResults")
    var totalResults: Int
) {

}