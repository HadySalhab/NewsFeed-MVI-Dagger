package com.android.myapplication.newsfeed.ui.headlines

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.android.myapplication.newsfeed.models.Article
import com.android.myapplication.newsfeed.repository.HeadlinesRepository
import com.android.myapplication.newsfeed.ui.BaseViewModel
import com.android.myapplication.newsfeed.ui.DataState
import com.android.myapplication.newsfeed.ui.headlines.state.HeadlinesStateEvent
import com.android.myapplication.newsfeed.ui.headlines.state.HeadlinesViewState
import com.android.myapplication.newsfeed.util.AbsentLiveData
import com.bumptech.glide.RequestManager
import javax.inject.Inject


class HeadlinesViewModel
@Inject
constructor(
    private val headlinesRepository: HeadlinesRepository,
    private val sharedPreferences: SharedPreferences,
    private val requestManager: RequestManager

) : BaseViewModel<HeadlinesStateEvent,HeadlinesViewState>()
{

    override fun initNewViewState(): HeadlinesViewState {
        return HeadlinesViewState()
    }

    override fun handleStateEvent(stateEvent: HeadlinesStateEvent): LiveData<DataState<HeadlinesViewState>> {
        when(stateEvent){
            is HeadlinesStateEvent.HeadlinesSearchEvent->{
                return headlinesRepository.getTopHeadlines(
                   stateEvent.country,
                    stateEvent.category
                )
            }
            is HeadlinesStateEvent.None->{
                return AbsentLiveData.create()
            }
        }
    }

    fun setQuery(query:String){
        val update = getCurrentViewStateOrNew()
        if(query.equals(update.headlinesFields.searchQuery)){
            return
        }
        update.headlinesFields.searchQuery = query
        _viewState.value = update
    }
    fun setHeadlineListData(headlinesList:List<Article>){
        val update = getCurrentViewStateOrNew()
        //we are not checking if it's the same list being passed, because RecycleView DiffUtil will take care of it
        update.headlinesFields.headlinesList = headlinesList
        _viewState.value = update
    }

     fun cancelActiveJobs(){
        headlinesRepository.cancelActiveJobs() //repository extends JobManager, cancelActiveJobs is part of the job Manager
        handlePendingData()
    }
    private fun handlePendingData(){
        setStateEvent(HeadlinesStateEvent.None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }


}