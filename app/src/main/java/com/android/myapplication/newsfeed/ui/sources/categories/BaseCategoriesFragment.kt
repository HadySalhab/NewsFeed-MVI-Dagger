package com.android.myapplication.newsfeed.ui.sources.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.myapplication.newsfeed.R
import com.android.myapplication.newsfeed.models.Article
import com.android.myapplication.newsfeed.models.Source
import com.android.myapplication.newsfeed.ui.sources.viewmodel.SourcesViewModel
import com.android.myapplication.newsfeed.util.TAG
import com.android.myapplication.newsfeed.viewmodels.ViewModelProviderFactory
import dagger.android.support.DaggerFragment
import javax.inject.Inject

abstract class BaseCategoriesFragment : DaggerFragment(),SourceListAdapter.Interaction {
    private var recyclerView: RecyclerView? = null
    private lateinit var sourceListAdapter: SourceListAdapter
    @Inject
    lateinit var  providerFactory: ViewModelProviderFactory
    lateinit var viewModel: SourcesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflateView(inflater, container)
        recyclerView = findRV(view)
        initRV()
        return view
    }

    abstract fun inflateView(inflater: LayoutInflater, container: ViewGroup?):View
    abstract fun findRV(view:View): RecyclerView


    override fun onItemSelected(position: Int, item: Source) {
        Log.d(TAG, "GeneralFragment: source list item selected: ${item} ")
        viewModel.updateArticleSourceViewState { articlesSourceField ->
            //reset
            with(articlesSourceField){
                articleList = ArrayList<Article>()
                errorScreenMsg = ""
                isQueryExhausted = false
                sourceId = item.id ?: ""
                sourceName = item.name ?: ""
                page = 1
                isQueryInProgress = false
            }
        }
        viewModel.updateSourceArticlesEvent(true)
        findNavController().navigate(R.id.action_sourcesFragment_to_articlesSourceFragment)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = activity?.run {
            ViewModelProvider(this,providerFactory).get(SourcesViewModel::class.java)
        }?:throw Exception ("Invalid Activity")
        Log.d(TAG, "BaseCategoriesFragment: onViewCreated: ${viewModel}")

        subscribeObservers()
        //the request has already been executed by the source fragment
    }

    private fun initRV() {
        recyclerView!!.apply {
            layoutManager = LinearLayoutManager(this@BaseCategoriesFragment.context)
            sourceListAdapter = SourceListAdapter(this@BaseCategoriesFragment)
            adapter = sourceListAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerView!!.adapter = null //to avoid memory leak
    }
    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewModelViewState ->
            viewModelViewState?.let {
                val sourceList = it.sourcesField.sourceList
                val generalSourceList = filterSourceList(sourceList)
                sourceListAdapter.submitList(generalSourceList)
            }
        })
    }
    abstract fun filterSourceList(sourceList:List<Source>):List<Source>

}