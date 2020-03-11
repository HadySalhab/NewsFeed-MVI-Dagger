package com.android.myapplication.newsfeed.ui.sources

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.android.myapplication.newsfeed.R
import com.android.myapplication.newsfeed.ui.BaseFragment
import com.android.myapplication.newsfeed.util.TAG
import com.android.myapplication.newsfeed.viewmodels.ViewModelProviderFactory
import java.lang.Exception
import javax.inject.Inject

class ArticlesSourceFragment : BaseFragment(){
    @Inject
    lateinit var  providerFactory: ViewModelProviderFactory
    lateinit var viewModel: SourcesViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        return inflater.inflate(R.layout.fragment_articles_sources,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = activity?.run {
            ViewModelProvider(this,providerFactory).get(SourcesViewModel::class.java)
        }?:throw Exception ("Invalid Activity")
        Log.d(TAG, "ArticlesSourceFragment: onViewCreated: ${viewModel} ")
        cancelActiveJobs()
    }

    override fun getFragmentId(): Int = R.id.articlesSourceFragment
    override fun cancelActiveJobs() = viewModel.cancelActiveJobs()


}