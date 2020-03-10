package com.android.myapplication.newsfeed.ui.sources

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.myapplication.newsfeed.models.Source
import com.android.myapplication.newsfeed.repository.SourceRepository
import com.android.myapplication.newsfeed.ui.BaseViewModel
import com.android.myapplication.newsfeed.ui.DataState
import com.android.myapplication.newsfeed.ui.Event
import com.android.myapplication.newsfeed.ui.sources.state.SourcesStateEvent
import com.android.myapplication.newsfeed.ui.sources.state.SourcesViewState
import com.android.myapplication.newsfeed.util.AbsentLiveData
import javax.inject.Inject

class SourcesViewModel
@Inject
constructor(
    private val sourcesRepository:SourceRepository
) : BaseViewModel<SourcesStateEvent,SourcesViewState>() {



    override fun handleStateEvent(stateEvent: SourcesStateEvent)=  when (stateEvent) {
            is SourcesStateEvent.SourcesSearchEvent -> {
                 sourcesRepository.getSources()
            }
            is SourcesStateEvent.None -> {
                 AbsentLiveData.create()
            }
        }


    override fun initNewViewState() = SourcesViewState()


    fun updateSourceViewState(operation:(SourcesViewState.SourcesField)->Unit)= with(getCurrentViewStateOrNew()){
            operation(sourcesField)
            setViewState(this)
        }


    fun cancelActiveJobs() {
        sourcesRepository.cancelActiveJobs() //repository extends JobManager, cancelActiveJobs is part of the job Manager
        handlePendingData()
    }


    private fun handlePendingData() = setStateEvent(SourcesStateEvent.None())


    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}