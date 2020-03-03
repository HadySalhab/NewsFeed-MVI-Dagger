package com.android.myapplication.newsfeed.ui.sources

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.android.myapplication.newsfeed.R
import com.android.myapplication.newsfeed.ui.sources.state.SourcesStateEvent
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class SourcesFragment : BaseSourcesFragment() {
    private var viewPager: ViewPager2? = null
    private var tabLayout: TabLayout? = null
    private var tabLayoutMediator:TabLayoutMediator?=null
    private var adapter:SourcesPagerAdapter?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_sources, container, false)
        viewPager = view.findViewById(R.id.viewpager)
        tabLayout = view.findViewById(R.id.tabs)
        initViewPager()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "SourcesFragment: onViewCreated: ${viewModel} ")
        subscribeObservers()
        executeRequest()
    }

    private fun executeRequest(){
        viewModel.executeQueryEvent.observe(viewLifecycleOwner, Observer { queryEvent->
            //we dont want the query event or request to be re-fired when the config change happens
            // queryEvent life is attached to the viewModel which has the life of the activity (until its finished)
            queryEvent.getContentIfNotHandled()?.let {
                Log.d(TAG, "SourcesFragment: executeRequest: $queryEvent")
                viewModel.setStateEvent(SourcesStateEvent.SourcesSearchEvent())
            }

        })
    }
    private fun subscribeObservers() {
        //SourceFragment is responsible of updating the viewState in the viewModel when the dataState returns
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState->
            dataState?.let {
                stateChangeListener?.onDataStateChange(dataState) //Listener(BaseActivity/Activity) will handle the loading, error dialog/toast, data response msg
                dataState.data?.let {
                    it.data?.let { eventViewState->
                        eventViewState.getContentIfNotHandled()?.let { networkViewState->
                            Log.d(TAG, "SourcesFragment: dataStateReturned: with data!=null")
                            viewModel.setSourceListData(networkViewState.sourcesField.sourceList)
                        }
                    }
                }
                it.error?.let{ errorEvent->
                    errorEvent.getContentIfNotHandled()?.let { stateError ->
                        viewModel.setErrorScreenMsg(stateError.response.message?:"")
                    }
                }
            }
        })

        //Observing the ViewState is the role of categories fragment
    }

    private fun initViewPager() {
        adapter = SourcesPagerAdapter(this)
        viewPager!!.adapter = adapter
        tabLayoutMediator = TabLayoutMediator(tabLayout!!, viewPager!!) { tab, position ->
            tab.text = getTabTitle(position)
        }
        (tabLayoutMediator)!!.attach()
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

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        viewPager!!.adapter = null
        viewPager = null
        tabLayout = null
        tabLayoutMediator!!.detach()
        tabLayoutMediator = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "SourceFragment:OnDestroy ")
    }
}
