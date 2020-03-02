package com.android.myapplication.newsfeed.ui.sources

import SourcesStateEvent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.myapplication.newsfeed.models.Article
import com.android.myapplication.newsfeed.models.Source
import com.android.myapplication.newsfeed.repository.SourceRepository
import com.android.myapplication.newsfeed.ui.BaseViewModel
import com.android.myapplication.newsfeed.ui.DataState
import com.android.myapplication.newsfeed.ui.Event
import com.android.myapplication.newsfeed.ui.headlines.state.HeadlinesStateEvent
import com.android.myapplication.newsfeed.ui.sources.state.SourcesViewState
import com.android.myapplication.newsfeed.util.AbsentLiveData
import javax.inject.Inject

class SourcesViewModel
@Inject
constructor(
    private val sourcesRepository:SourceRepository
) : BaseViewModel<SourcesStateEvent,SourcesViewState>() {

    private val _executeQueryEvent = MutableLiveData<Event<Boolean>>(Event(true)) //this event will set to true when this viewModel is first created
    val executeQueryEvent: LiveData<Event<Boolean>>
        get() = _executeQueryEvent


    override fun handleStateEvent(stateEvent: SourcesStateEvent): LiveData<DataState<SourcesViewState>> {
        when (stateEvent) {
            is SourcesStateEvent.SourcesSearchEvent -> {
                return sourcesRepository.getSources()
            }
            is SourcesStateEvent.None -> {
                return AbsentLiveData.create()
            }
        }
    }

    override fun initNewViewState(): SourcesViewState {
       return SourcesViewState()
    }



    fun setErrorScreenMsg(errorScreenMsg:String){
        val update = getCurrentViewStateOrNew()
        if (errorScreenMsg.equals(update.sourcesField.errorScreenMsg)) {
            return
        }
        update.sourcesField.errorScreenMsg = errorScreenMsg
        _viewState.value = update
    }

    fun setSourceListData(sourcesList: List<Source>) {
        val update = getCurrentViewStateOrNew()
        //we are not checking if it's the same list being passed, because RecycleView DiffUtil will take care of it
        update.sourcesField.sourceList = sourcesList
        _viewState.value = update
    }

    fun cancelActiveJobs() {
        sourcesRepository.cancelActiveJobs() //repository extends JobManager, cancelActiveJobs is part of the job Manager
        handlePendingData()
    }


    private fun handlePendingData() {
        setStateEvent(SourcesStateEvent.None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}