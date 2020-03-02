package com.android.myapplication.newsfeed.ui.sources.categories

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.android.myapplication.newsfeed.R
import com.android.myapplication.newsfeed.ui.sources.BaseCategoriesSourcesFragment

/**
 * A simple [Fragment] subclass.
 */
class BusinessFragment : BaseCategoriesSourcesFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_business, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "BusinessFragment: onViewCreated: ${viewModel}")
    }

}
