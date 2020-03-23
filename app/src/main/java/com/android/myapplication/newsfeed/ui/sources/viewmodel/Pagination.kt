package com.android.myapplication.newsfeed.ui.sources.viewmodel

import com.android.myapplication.newsfeed.ui.sources.state.SourcesStateEvent
import com.android.myapplication.newsfeed.ui.sources.state.SourcesViewState
import com.android.myapplication.newsfeed.util.findCommonAndReplace

const val EMPTY_STRING = ""
//country and category can be passed at runtime
fun SourcesViewModel.loadFirstPage(sourceId:String) {
    updateArticleSourceViewState { sourceArticlesFields->
        with(sourceArticlesFields){
            isQueryInProgress = true
            isQueryExhausted = false
            page = 1
        }
    }
    setStateEvent(
        SourcesStateEvent.SourceArticlesEvent(
            sourceId,
            1
        )
    )
}

//country & category are already known
fun SourcesViewModel.loadNextPage() {
    // we should not get the next page when the query is exhausted or when the query is still in progress
    if (!getVSArticlesSources().isQueryExhausted && !getVSArticlesSources().isQueryInProgress && getVSArticlesSources().errorScreenMsg.isEmpty()) {
        updateArticleSourceViewState { sourceArticlesFields->
            with(sourceArticlesFields){
                page++
                isQueryInProgress = true
            }
        }

        with(getVSArticlesSources()) {
            setStateEvent(
                SourcesStateEvent.SourceArticlesEvent(
                    sourceId,
                    page
                )
            )
        }
    }
}

fun SourcesViewModel.handlePaginationSuccessResult(networkVS: SourcesViewState) {
    //if the first page, replace all list item, else append to the list item
    val networkHeadlineFields = networkVS.articlesSourceField
    updateArticleSourceViewState { sourceArticlesFields->
        with(sourceArticlesFields){
            isQueryExhausted = networkHeadlineFields.isQueryExhausted
            errorScreenMsg =  ""
            if(page==1){
                articleList = networkHeadlineFields.articleList
            }else{
                val commonArticles = networkHeadlineFields.articleList.intersect(articleList)
                if (!commonArticles.isNullOrEmpty()) {
                    commonArticles.forEach { commonArticle ->
                        articleList = ArrayList(articleList.toList().findCommonAndReplace(commonArticle))
                    }
                }else{
                    articleList += networkHeadlineFields.articleList
                }
            }
        }
    }
}