package com.android.myapplication.newsfeed.ui.favorites

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.android.myapplication.newsfeed.R
import dagger.android.support.DaggerFragment

abstract class BaseFavoritesFragment : DaggerFragment (){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupActionBarWithNavController(R.id.favoritesFragment,activity as AppCompatActivity)

    }

    fun setupActionBarWithNavController(fragmentId:Int,activity: AppCompatActivity){
        val appBarConfiguration = AppBarConfiguration(setOf(fragmentId))
        NavigationUI.setupActionBarWithNavController(
            activity,
            findNavController(),
            appBarConfiguration
        )
    }
    fun cancelActiveJobs(){
        //viewModel.cancelActiveJobs()
    }
}