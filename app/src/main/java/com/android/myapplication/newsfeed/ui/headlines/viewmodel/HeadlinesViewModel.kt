package com.android.myapplication.newsfeed.ui.headlines.viewmodel

import android.content.SharedPreferences
import com.android.myapplication.newsfeed.repository.HeadlinesRepository
import com.android.myapplication.newsfeed.ui.BaseViewModel
import com.android.myapplication.newsfeed.ui.headlines.state.HeadlinesStateEvent
import com.android.myapplication.newsfeed.ui.headlines.state.HeadlinesViewState
import com.android.myapplication.newsfeed.util.AbsentLiveData
import com.android.myapplication.newsfeed.util.getCategory
import com.android.myapplication.newsfeed.util.getCountry
import com.android.myapplication.newsfeed.util.saveCountryAndCategory
import javax.inject.Inject


class HeadlinesViewModel
@Inject
constructor(
    private val headlinesRepository: HeadlinesRepository,
    private val sharedPreferences: SharedPreferences,
    private val editor: SharedPreferences.Editor
) : BaseViewModel<HeadlinesStateEvent, HeadlinesViewState>() {

    init {
        updateViewState { headlinesFields->
            headlinesFields.country = sharedPreferences.getCountry()
            headlinesFields.category = sharedPreferences.getCategory()
        }
    }
    override fun initNewViewState(): HeadlinesViewState =  HeadlinesViewState()

    override fun handleStateEvent(stateEvent: HeadlinesStateEvent) = when (stateEvent) {
            is HeadlinesStateEvent.HeadlinesSearchEvent -> {
                 with(stateEvent,{
                    headlinesRepository.getTopHeadlines(
                        country,category,searchQuery,page
                    )
                })
            }
            is HeadlinesStateEvent.None -> {
               AbsentLiveData.create()
            }
        }

    fun cancelActiveJobs() {
        headlinesRepository.cancelActiveJobs() //repository extends JobManager, cancelActiveJobs is part of the job Manager
        setStateEvent(HeadlinesStateEvent.None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }

    fun saveCategoryAndCountry(country:String,category:String){
        editor.saveCountryAndCategory(country,category)
    }

}