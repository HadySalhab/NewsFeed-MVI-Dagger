package com.android.myapplication.newsfeed.ui.sources

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.android.myapplication.newsfeed.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_sources.*

class SourcesFragment : BaseSourcesFragment (){
    private lateinit var viewPager:ViewPager2
    private lateinit var tabLayout: TabLayout
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_sources,container,false)
        viewPager = view.findViewById(R.id.viewpager)
        tabLayout = view.findViewById(R.id.tabs)
        initViewPager()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initViewPager(){
        viewPager.adapter = SourcesPagerAdapter(this)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getTabTitle(position)
        }.attach()
    }
 /*   private fun getTabIcon(position: Int): Int {
        return when (position) {
            GENERAL_SOURCE_PAGE_INDEX -> R.drawable.garden_tab_selector
            BUSINESS_SOURCE_PAGE_INDEX -> R.drawable.plant_list_tab_selector
            ENTERTAINMENT_SOURCE_PAGE_INDEX -> R.drawable.garden_tab_selector
            HEALTH_SOURCE_PAGE_INDEX -> R.drawable.plant_list_tab_selector
            SCIENCE_SOURCE_PAGE_INDEX -> R.drawable.garden_tab_selector
            SPORTS_SOURCE_PAGE_INDEX -> R.drawable.plant_list_tab_selector
            TECHNOLOGY_SOURCE_PAGE_INDEX -> R.drawable.garden_tab_selector
            else -> throw IndexOutOfBoundsException()
        }
    }*/

    private fun getTabTitle(position: Int): String? {
        return when (position) {
            GENERAL_SOURCE_PAGE_INDEX -> getString(R.string.tab_general)
            BUSINESS_SOURCE_PAGE_INDEX -> getString(R.string.tab_business)
            ENTERTAINMENT_SOURCE_PAGE_INDEX -> getString(R.string.tab_entertainment)
            HEALTH_SOURCE_PAGE_INDEX -> getString(R.string.tab_health)
            SCIENCE_SOURCE_PAGE_INDEX -> getString(R.string.tab_science)
            SPORTS_SOURCE_PAGE_INDEX -> getString(R.string.tab_sports)
            TECHNOLOGY_SOURCE_PAGE_INDEX -> getString(R.string.tab_technology)
            else -> null
        }
    }
}
