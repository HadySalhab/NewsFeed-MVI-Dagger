package com.android.myapplication.newsfeed.ui.headlines.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.myapplication.newsfeed.models.Article
import com.android.myapplication.newsfeed.repository.HeadlinesRepository
import com.android.myapplication.newsfeed.ui.BaseViewModel
import com.android.myapplication.newsfeed.ui.DataState
import com.android.myapplication.newsfeed.ui.headlines.state.HeadlinesStateEvent
import com.android.myapplication.newsfeed.ui.headlines.state.HeadlinesViewState
import com.android.myapplication.newsfeed.ui.Event
import com.android.myapplication.newsfeed.util.AbsentLiveData
import com.bumptech.glide.RequestManager
import javax.inject.Inject


class HeadlinesViewModel
@Inject
constructor(
    private val headlinesRepository: HeadlinesRepository,
    private val sharedPreferences: SharedPreferences,
    private val requestManager: RequestManager

) : BaseViewModel<HeadlinesStateEvent, HeadlinesViewState>() {
    override fun initNewViewState(): HeadlinesViewState {
        return HeadlinesViewState()
    }

    override fun handleStateEvent(stateEvent: HeadlinesStateEvent): LiveData<DataState<HeadlinesViewState>> {
        when (stateEvent) {
            is HeadlinesStateEvent.HeadlinesSearchEvent -> {
                return headlinesRepository.getTopHeadlines(
                    stateEvent.country,
                    stateEvent.category,
                    stateEvent.searchQuery,
                    stateEvent.page
                )
            }
            is HeadlinesStateEvent.None -> {
                return AbsentLiveData.create()
            }
        }
    }



    fun cancelActiveJobs() {
        headlinesRepository.cancelActiveJobs() //repository extends JobManager, cancelActiveJobs is part of the job Manager
        handlePendingData()
    }

    private fun handlePendingData() {
        setStateEvent(HeadlinesStateEvent.None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }

}