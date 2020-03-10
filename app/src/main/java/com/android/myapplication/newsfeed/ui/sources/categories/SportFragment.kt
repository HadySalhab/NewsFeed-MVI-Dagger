package com.android.myapplication.newsfeed.ui.sources.categories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.android.myapplication.newsfeed.R
import com.android.myapplication.newsfeed.models.Source
import com.android.myapplication.newsfeed.util.SPORTS

/**
 * A simple [Fragment] subclass.
 */
class SportFragment : BaseCategoriesFragment() {

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup?): View =inflater.inflate(R.layout.fragment_sport,container,false)

    override fun findRV(view:View): RecyclerView = view.findViewById(R.id.rv_sport)

    override fun filterSourceList(sourceList: List<Source>): List<Source>  = sourceList.filter { source ->
        source.category.equals(SPORTS)
    }
}
