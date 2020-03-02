package com.android.myapplication.newsfeed.ui.headlines

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.android.myapplication.newsfeed.R
import com.android.myapplication.newsfeed.ui.headlines.state.HeadlinesStateEvent
import kotlinx.android.synthetic.main.fragment_sources.*

class HeadlineFragment  : BaseHeadlineFragment(){
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        return inflater.inflate(R.layout.fragment_headlines,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState) //BaseHeadlineFragment implementation and Fragment()
        subscribeObservers()
        executeRequest()
    }
    private fun executeRequest(){
        viewModel.setStateEvent(HeadlinesStateEvent.HeadlinesSearchEvent())
    }

    private fun subscribeObservers(){
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState->
            dataState?.let {
                stateChangeListener.onDataStateChange(dataState) //let the listener invoke their onDataStateChange impl (error, loading ,data response)
                //this component handle the data data
                dataState.data?.let {
                    it.data?.let { event ->
                        event.getContentIfNotHandled()?.let {viewState->
                            Log.d(TAG, "HeadlineFragment: viewState: $viewState")
                            //we are updating a field in the viewState, which will update the viewState itself
                            // and fire observers
                            viewModel.setHeadlineListData(viewState.headlinesFields.headlinesList)
                        }
                    }
                }
            }
        })
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState->
            Log.d(TAG, "HeadlineFragment: viewState: $viewState")
        })
    }
}