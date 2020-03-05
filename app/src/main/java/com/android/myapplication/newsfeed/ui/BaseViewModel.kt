package com.android.myapplication.newsfeed.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel


abstract class BaseViewModel<StateEvent, ViewState> : ViewModel()
{

    val TAG: String = "AppDebug"

    protected val _stateEvent: MutableLiveData<StateEvent> = MutableLiveData()
    protected val _viewState: MutableLiveData<ViewState> = MutableLiveData()

    private val _executeQueryEvent = MutableLiveData<Event<Boolean>>(Event(true)) //this event will set to true when this viewModel is first created
    val executeQueryEvent: LiveData<Event<Boolean>>
        get() = _executeQueryEvent


    val viewState: LiveData<ViewState>
        get() = _viewState

    val dataState: LiveData<DataState<ViewState>> = Transformations
        .switchMap(_stateEvent){stateEvent ->
            stateEvent?.let {
                handleStateEvent(stateEvent)
            }
        }

    fun setStateEvent(event: StateEvent){
        _stateEvent.value = event
    }

    //create newView state or get the existing one
    fun getCurrentViewStateOrNew(): ViewState{
        val value = viewState.value?.let{
            it
        }?: initNewViewState()
        return value
    }

    fun setViewState(viewState:ViewState){
        _viewState.value = viewState
    }

    abstract fun handleStateEvent(stateEvent: StateEvent): LiveData<DataState<ViewState>>

    abstract fun initNewViewState(): ViewState

}