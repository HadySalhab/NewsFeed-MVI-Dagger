package com.android.myapplication.newsfeed.ui.sources.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.myapplication.newsfeed.repository.SourceRepository
import com.android.myapplication.newsfeed.ui.BaseViewModel
import com.android.myapplication.newsfeed.ui.DataState
import com.android.myapplication.newsfeed.ui.sources.state.SourcesStateEvent
import com.android.myapplication.newsfeed.ui.sources.state.SourcesViewState
import javax.inject.Inject

class SourcesViewModel
@Inject
constructor(
    private val sourcesRepository:SourceRepository
) : BaseViewModel<SourcesStateEvent,SourcesViewState>() {

    private val _sourceArticlesEvent = MutableLiveData<Boolean>(true) //this event will set to true when this viewModel is first created
    val sourceArticlesEvent: LiveData<Boolean>
        get() = _sourceArticlesEvent

    public fun updateSourceArticlesEvent(boolean: Boolean){
        _sourceArticlesEvent.value = boolean
    }

    override fun handleStateEvent(stateEvent: SourcesStateEvent)=  when (stateEvent) {
        is SourcesStateEvent.SourcesSearchEvent -> {
            sourcesRepository.getSources()
        }
        is SourcesStateEvent.None -> {
            object : LiveData<DataState<SourcesViewState>>() {
                override fun onActive() {
                    super.onActive()
                    value = DataState.none()
                }
            }
        }
        is SourcesStateEvent.SourceArticlesEvent->{
            with(stateEvent,{
                sourcesRepository.getTopHeadlinesBySource(
                    sourceId = sourceId,page = page
                )
            })
        }
        is SourcesStateEvent.SourceArticlesAddToFavEvent -> {
            sourcesRepository.insertArticleToDB(stateEvent.article)
        }
        is SourcesStateEvent.SourceArticlesRemoveFromFavEvent -> {
            sourcesRepository.deleteArticleFromDB(stateEvent.article)
        }
        is SourcesStateEvent.SourceArticlesCheckFavEvent->{
            sourcesRepository.checkFavorite(stateEvent.articles,stateEvent.isQueryExhausted)
        }

    }

    override fun initNewViewState() = SourcesViewState()


    fun updateSourceViewState(operation:(SourcesViewState.SourcesField)->Unit)= with(getCurrentViewStateOrNew()){
            operation(sourcesField)
            setViewState(this)
        }
    fun updateArticleSourceViewState(operation:(SourcesViewState.ArticlesSourceField)->Unit) = with(getCurrentViewStateOrNew()){
        operation(articlesSourceField)
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
    fun getVSArticlesSources() = getCurrentViewStateOrNew().articlesSourceField
}