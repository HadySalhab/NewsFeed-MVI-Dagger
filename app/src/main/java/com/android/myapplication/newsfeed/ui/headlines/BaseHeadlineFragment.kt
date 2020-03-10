package com.android.myapplication.newsfeed.ui.headlines

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
import com.android.myapplication.newsfeed.ui.headlines.viewmodel.HeadlinesViewModel
import com.android.myapplication.newsfeed.viewmodels.ViewModelProviderFactory
import dagger.android.support.DaggerFragment
import java.lang.Exception
import javax.inject.Inject

abstract class BaseHeadlineFragment : DaggerFragment (){

    val TAG:String = "AppDebug"

    @Inject
    lateinit var  providerFactory: ViewModelProviderFactory

    lateinit var viewModel: HeadlinesViewModel

     var stateChangeListener: DataStateChangeListener?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupActionBarWithNavController(R.id.headlineFragment,activity as AppCompatActivity)

        //run, because we want implicit reference to the activity, to ensure one store owner,
        // that way the same viewModel instance is going to be injected
        //the viewModel life is wired to its owner, in this case the MainActivity
        viewModel = activity?.run {
            ViewModelProvider(this,providerFactory).get(HeadlinesViewModel::class.java)
        }?:throw Exception ("Invalid Activity")
        cancelActiveJobs()

    }

    //by cancelling active job, the StateEvent will be reset to None()
    // and the DataState -> DataState(null, Loading(false), null)
    fun cancelActiveJobs()= viewModel.cancelActiveJobs()


    fun setupActionBarWithNavController(fragmentId:Int,activity: AppCompatActivity) = NavigationUI.setupActionBarWithNavController(
            activity,
            findNavController(),
            AppBarConfiguration(setOf(fragmentId))
        )
    //getting the activity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            stateChangeListener = context as DataStateChangeListener
        }catch(e: ClassCastException){
            Log.e(TAG, "$context must implement DataStateChangeListener" )
        }
    }

    override fun onDetach() {
        super.onDetach()
        stateChangeListener = null
    }
}