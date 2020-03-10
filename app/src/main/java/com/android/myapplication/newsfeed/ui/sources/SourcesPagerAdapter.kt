package com.android.myapplication.newsfeed.ui.sources

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.android.myapplication.newsfeed.ui.sources.categories.*

const val GENERAL_SOURCE_PAGE_INDEX = 0
const val BUSINESS_SOURCE_PAGE_INDEX = 1
const val ENTERTAINMENT_SOURCE_PAGE_INDEX = 2
const val HEALTH_SOURCE_PAGE_INDEX = 3
const val SCIENCE_SOURCE_PAGE_INDEX = 4
const val SPORTS_SOURCE_PAGE_INDEX = 5
const val TECHNOLOGY_SOURCE_PAGE_INDEX = 6


class SourcesPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    /**
     * Mapping of the ViewPager page indexes to their respective Fragments
     */
    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        GENERAL_SOURCE_PAGE_INDEX to { GeneralFragment() },
        BUSINESS_SOURCE_PAGE_INDEX to { BusinessFragment() },
        ENTERTAINMENT_SOURCE_PAGE_INDEX to { EntertainmentFragment() },
        HEALTH_SOURCE_PAGE_INDEX to { HealthFragment() },
        SCIENCE_SOURCE_PAGE_INDEX to { ScienceFragment() },
        SPORTS_SOURCE_PAGE_INDEX to { SportFragment() },
        TECHNOLOGY_SOURCE_PAGE_INDEX to { TechnologyFragment() }
    )

    override fun getItemCount() = tabFragmentsCreators.size

    override fun createFragment(position: Int) = tabFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()

}