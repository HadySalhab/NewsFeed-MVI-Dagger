package com.android.myapplication.newsfeed.ui.favorites

import android.util.Log
import androidx.lifecycle.LiveData
import com.android.myapplication.newsfeed.repository.FavoritesRepository
import com.android.myapplication.newsfeed.ui.BaseViewModel
import com.android.myapplication.newsfeed.ui.DataState
import com.android.myapplication.newsfeed.ui.headlines.state.FavoritesStateEvent
import com.android.myapplication.newsfeed.ui.headlines.state.FavoritesViewState
import com.android.myapplication.newsfeed.util.AbsentLiveData
import com.android.myapplication.newsfeed.util.TAG
import javax.inject.Inject

class FavoritesViewModel
@Inject
constructor(
    private val favoritesRepository: FavoritesRepository
) : BaseViewModel<FavoritesStateEvent, FavoritesViewState>() {
    override fun handleStateEvent(stateEvent: FavoritesStateEvent): LiveData<DataState<FavoritesViewState>> =
        when (stateEvent) {
            is FavoritesStateEvent.GetFavoritesEvent -> {
                favoritesRepository.getFavorites()
            }
            is FavoritesStateEvent.DeleteFromFavEvent -> {
               favoritesRepository.deleteArticle(stateEvent.article)
            }
            is FavoritesStateEvent.None -> {
                AbsentLiveData.create<DataState<FavoritesViewState>>()
            }
        }

    override fun initNewViewState(): FavoritesViewState = FavoritesViewState()

    fun cancelActiveJobs() {
        favoritesRepository.cancelActiveJobs() //repository extends JobManager, cancelActiveJobs is part of the job Manager
        setStateEvent(FavoritesStateEvent.None())
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared: ")
        cancelActiveJobs()
    }

    fun getVSfavorites() = getCurrentViewStateOrNew().favoritesFields
    fun updateViewState(operation:(FavoritesViewState.FavoritesFields)->Unit) = with(getCurrentViewStateOrNew()){
        operation(favoritesFields)
        setViewState(this)
    }

}