package com.android.myapplication.newsfeed.ui.headlines

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.myapplication.newsfeed.R
import com.android.myapplication.newsfeed.models.Article
import com.android.myapplication.newsfeed.ui.headlines.state.HeadlinesStateEvent
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.fragment_headlines.*
import javax.inject.Inject

class HeadlineFragment : BaseHeadlineFragment(), HeadlinesListAdapter.Interaction {

    @Inject
    lateinit var requestManager: RequestManager

    private lateinit var headlinesAdapter: HeadlinesListAdapter
    private lateinit var rv: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_headlines, container, false)
        rv = view.findViewById(R.id.rv_headlines)
        initRV()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(
            view,
            savedInstanceState
        ) //BaseHeadlineFragment implementation and Fragment()
        subscribeObservers()
        executeRequest()
    }

    private fun executeRequest() {
        // no point of firing the event everytime we rotate or change graph
        //as long as the viewModel is alive, no need to re-fire
        //viewModel life span is tied to the MainActivity (store)
        //  queryEvent will be held in the viewModel and set to true when the viewModel is first created
        // when we rotate , the queryEvent liveData will still hold the same object/event, which is already been handled
        // NOTE: even though the DataState member variables are wrapped in event,
        // it will be refreshed because its event object are being updated, in the networkBoundResources and repositories every time we fire this request
        viewModel.executeQueryEvent.observe(viewLifecycleOwner, Observer { queryEvent->
                queryEvent.getContentIfNotHandled()?.let { //only proceed if this query has never been handled
                    viewModel.setStateEvent(HeadlinesStateEvent.HeadlinesSearchEvent())
                }
            })


    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            dataState?.let {
                stateChangeListener.onDataStateChange(dataState) //let the listener invoke their onDataStateChange impl (error, loading ,data response)
                //this component handle the data data
                dataState.data?.let {
                    it.data?.let { event ->
                        event.getContentIfNotHandled()?.let { viewState ->
                            Log.d(TAG, "HeadlineFragment: viewState: $viewState")
                            //we are updating a field in the viewState, which will update the viewState itself
                            // and fire observers
                            viewModel.setHeadlineListData(viewState.headlinesFields.headlinesList)
                        }
                    }
                }
            }
        })
        //As soon as the HeadlineFrag is created , it will receive the viewState (if available) in the ViewModel
        // and everTime the viewState is changed
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            Log.d(TAG, "HeadlineFragment: viewState: $viewState")
            viewState?.let {
                headlinesAdapter.submitList(
                    list = it.headlinesFields.headlinesList,
                    isQueryExhausted = true
                )
            }
        })
    }

    private fun initRV() {
        rv.apply {
            layoutManager = LinearLayoutManager(this@HeadlineFragment.context)
            headlinesAdapter = HeadlinesListAdapter(this@HeadlineFragment, requestManager)

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if (lastPosition == headlinesAdapter.itemCount.minus(1)) {
                        Log.d(TAG, "HeadlineFragment: load next page...")
                    }
                }
            })
            adapter = headlinesAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        rv_headlines.adapter = null //to avoid memory leak
    }

    override fun onItemSelected(position: Int, item: Article) {
        Log.d(TAG, "onItemSelected: position,article: ${position}, ${item} ")
        fireIntent(item)
    }

    private fun fireIntent(item: Article) {
        val url = item.url
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }
}