package com.android.myapplication.newsfeed.ui.sources.categories

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.android.myapplication.newsfeed.R
import com.android.myapplication.newsfeed.models.Source
import com.android.myapplication.newsfeed.ui.sources.BaseCategoriesSourcesFragment
import com.android.myapplication.newsfeed.util.SourcesCategories

/**
 * A simple [Fragment] subclass.
 */
class BusinessFragment : BaseCategoriesFragment() {

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup?): View =inflater.inflate(R.layout.fragment_business,container,false)

    override fun findRV(view:View): RecyclerView = view.findViewById(R.id.rv_business)

    override fun filterSourceList(sourceList: List<Source>): List<Source>  = sourceList.filter { source ->
        source.category.equals(SourcesCategories.BUSINESS)
    }

}
