package com.android.myapplication.newsfeed.ui.sources

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.android.myapplication.newsfeed.R
import com.android.myapplication.newsfeed.ui.DataStateChangeListener
import com.android.myapplication.newsfeed.viewmodels.ViewModelProviderFactory
import java.lang.Exception
import javax.inject.Inject

abstract class BaseSourcesFragment : BaseCategoriesSourcesFragment (){


    lateinit var stateChangeListener: DataStateChangeListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBarWithNavController(R.id.sourcesFragment,activity as AppCompatActivity)
        cancelActiveJobs()
    }

    fun setupActionBarWithNavController(fragmentId:Int,activity:AppCompatActivity){
        val appBarConfiguration = AppBarConfiguration(setOf(fragmentId))
        NavigationUI.setupActionBarWithNavController(
            activity,
            findNavController(),
            appBarConfiguration
        )
    }
    fun cancelActiveJobs(){
        viewModel.cancelActiveJobs()
    }
    //getting the activity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            stateChangeListener = context as DataStateChangeListener
        }catch(e: ClassCastException){
            Log.e(TAG, "$context must implement DataStateChangeListener" )
        }
    }
}