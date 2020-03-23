package com.android.myapplication.newsfeed.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.android.myapplication.newsfeed.R
import com.android.myapplication.newsfeed.util.TAG
import dagger.android.support.DaggerFragment

abstract class BaseFragment : DaggerFragment() {
    var stateChangeListener: DataStateChangeListener? =null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
       super.onViewCreated(view, savedInstanceState)
       setupActionBarWithNavController(getFragmentId(),activity as AppCompatActivity)
   }
    protected abstract fun getFragmentId():Int

    fun setupActionBarWithNavController(fragmentId: Int, activity: AppCompatActivity) {
        val sets = mutableSetOf<Int>()
        if(fragmentId != R.id.articlesSourceFragment){
            sets.add(fragmentId)
        }
        NavigationUI.setupActionBarWithNavController(
            activity,
            findNavController(),
            AppBarConfiguration(sets)

    )
    }

    abstract fun cancelActiveJobs()
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

