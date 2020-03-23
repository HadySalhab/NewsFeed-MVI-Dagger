package com.android.myapplication.newsfeed.ui.sources

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.myapplication.newsfeed.R
import com.android.myapplication.newsfeed.models.Article
import com.android.myapplication.newsfeed.ui.BaseFragment
import com.android.myapplication.newsfeed.ui.DataState
import com.android.myapplication.newsfeed.ui.headlines.HeadlinesListAdapter
import com.android.myapplication.newsfeed.ui.sources.state.SourcesStateEvent
import com.android.myapplication.newsfeed.ui.sources.state.SourcesViewState
import com.android.myapplication.newsfeed.ui.sources.viewmodel.SourcesViewModel
import com.android.myapplication.newsfeed.ui.sources.viewmodel.handlePaginationSuccessResult
import com.android.myapplication.newsfeed.ui.sources.viewmodel.loadFirstPage
import com.android.myapplication.newsfeed.ui.sources.viewmodel.loadNextPage
import com.android.myapplication.newsfeed.util.TAG
import com.android.myapplication.newsfeed.util.findCommonAndReplace
import com.android.myapplication.newsfeed.util.isNetworkAvailable
import com.android.myapplication.newsfeed.viewmodels.ViewModelProviderFactory
import com.bumptech.glide.RequestManager
import javax.inject.Inject

class ArticlesSourceFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener,
    HeadlinesListAdapter.Interaction {
    @Inject
    lateinit var requestManager: RequestManager
    @Inject
    lateinit var  providerFactory: ViewModelProviderFactory
    lateinit var viewModel: SourcesViewModel
    private lateinit var sourceArticlesAdapter: HeadlinesListAdapter
    private  var recyclerView : RecyclerView?=null
    private  var tv_error: TextView?=null
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        setHasOptionsMenu(true)
        val view =inflater.inflate(R.layout.fragment_articles_sources,container,false)
        tv_error = view.findViewById(R.id.tv_article_source_error)
        recyclerView = view.findViewById(R.id.rv_headlines)
        swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh).apply {
            setOnRefreshListener(this@ArticlesSourceFragment)
        }

        initRV()
        Log.d(TAG, "ARTICLE SOURCE FRAGMENT onCreateView: ")

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = activity?.run {
            ViewModelProvider(this,providerFactory).get(SourcesViewModel::class.java)
        }?:throw Exception ("Invalid Activity")
        Log.d(TAG, "ARTICLE SOURCE FRAGMENT onViewCreated: ")
        cancelActiveJobs()
        subscribeObservers()
        executeRequest()
        (activity as AppCompatActivity).supportActionBar?.title = viewModel.getVSArticlesSources().sourceName
    }

    override fun getFragmentId(): Int = R.id.articlesSourceFragment
    override fun cancelActiveJobs() {
        if(::viewModel.isInitialized) viewModel.cancelActiveJobs()
    }

    override fun onRefresh() {
        with(viewModel){
            with(getVSArticlesSources()){
                loadFirstPage(sourceId)
            }
        }
        swipeRefreshLayout.isRefreshing = false
    }

    private fun initRV() {
        recyclerView!!.apply {
            layoutManager = LinearLayoutManager(this@ArticlesSourceFragment.context)
            sourceArticlesAdapter = HeadlinesListAdapter(this@ArticlesSourceFragment, requestManager)

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if (lastPosition == sourceArticlesAdapter.itemCount.minus(1)) {
                        Log.d(TAG, "HeadlineFragment: load next page...")
                        if(requireActivity().application.isNetworkAvailable() && viewModel.getVSArticlesSources().page >1 && viewModel.getVSArticlesSources().errorScreenMsg.isNotEmpty()){
                            viewModel.updateArticleSourceViewState { sourceArticlesField ->
                                sourceArticlesField.errorScreenMsg = ""
                            }
                        }
                        viewModel.loadNextPage()
                    }
                }
            })
            adapter = sourceArticlesAdapter
        }
    }

    override fun onItemSelected(position: Int, item: Article) {
        fireIntent(item)
    }

    override fun onFavIconClicked(isFavorite: Boolean, item: Article) {
        viewModel.updateArticleSourceViewState { sourceArticlesField ->
            val favArticle = item.copy(isFavorite = true)
            sourceArticlesField.articleList = sourceArticlesField.articleList.findCommonAndReplace(favArticle)
        }
        if(isFavorite) {
            viewModel.setStateEvent(SourcesStateEvent.SourceArticlesAddToFavEvent(item))
        }
        else{
            viewModel.setStateEvent(SourcesStateEvent.SourceArticlesRemoveFromFavEvent(item))
        }

    }
    private fun fireIntent(item: Article) = with(Intent(Intent.ACTION_VIEW)){
        data = Uri.parse(item.url)
        startActivity(this)
    }

    private fun subscribeObservers() = with(viewModel){
        dataState.observe(viewLifecycleOwner, Observer { dataState ->
            dataState?.let {
                stateChangeListener?.onDataStateChange(it)
                handlePagination(it)
            }
        })
        viewState.observe(viewLifecycleOwner, Observer { viewModelViewState ->
            Log.d(TAG, "HeadlineFragment: viewState observer: ${viewModelViewState}")
            viewModelViewState?.let {
                with(it.articlesSourceField){
                    sourceArticlesAdapter.submitList(
                        list = articleList,
                        isQueryExhausted = isQueryExhausted,
                        page = page
                    )
                    if(articleList.isNullOrEmpty() && !errorScreenMsg.isEmpty()){
                        recyclerView!!.visibility  = View.GONE
                        tv_error!!.apply {
                            visibility = View.VISIBLE
                            setText( errorScreenMsg)
                        }
                    }else{
                        tv_error!!.visibility = View.GONE
                        recyclerView!!.visibility  = View.VISIBLE
                    }
                }
            }
        })
    }
    private fun handlePagination(dataState: DataState<SourcesViewState>){
        viewModel.updateArticleSourceViewState { sourceArticlesFields->
            sourceArticlesFields.isQueryInProgress = dataState.loading.isLoading
        }

        dataState.data?.let {
            it.data?.let { eventViewState ->
                eventViewState.getContentIfNotHandled()?.let { networkViewState ->
                    viewModel.handlePaginationSuccessResult(networkViewState)
                }
            }
        }
        dataState.error?.let { errorEvent ->
            errorEvent.peekContent()?.let{ stateError->
                viewModel.updateArticleSourceViewState { sourceArticlesFields->
                    sourceArticlesFields.errorScreenMsg = stateError.response.message?:""
                }
            }
        }
    }
    private fun executeRequest() =     viewModel.sourceArticlesEvent.observe(viewLifecycleOwner, Observer { event->
        if(event) { //only proceed if this query has never been handled
            with(viewModel) {
                with(getVSArticlesSources()) {
                    loadFirstPage(sourceId)
                }
            }
            viewModel.updateSourceArticlesEvent(false)
        }else{
            if(!viewModel.getVSArticlesSources().articleList.isNullOrEmpty()) {
                viewModel.setStateEvent(SourcesStateEvent.SourceArticlesCheckFavEvent(viewModel.getVSArticlesSources().articleList,viewModel.getVSArticlesSources().isQueryExhausted))
            }
        }
    })




}