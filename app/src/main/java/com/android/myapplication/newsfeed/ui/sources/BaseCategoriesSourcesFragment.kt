package com.android.myapplication.newsfeed.ui.sources

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.android.myapplication.newsfeed.viewmodels.ViewModelProviderFactory
import dagger.android.support.DaggerFragment
import java.lang.Exception
import javax.inject.Inject

abstract class BaseCategoriesSourcesFragment : DaggerFragment(){
    val TAG:String = "AppDebug"

    @Inject
    lateinit var  providerFactory: ViewModelProviderFactory
    lateinit var viewModel: SourcesViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = activity?.run {
            ViewModelProvider(this,providerFactory).get(SourcesViewModel::class.java)
        }?:throw Exception ("Invalid Activity")
    }
}