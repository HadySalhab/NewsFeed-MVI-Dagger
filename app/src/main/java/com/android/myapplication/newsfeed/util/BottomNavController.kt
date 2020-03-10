package com.android.myapplication.newsfeed.util

import android.app.Activity
import android.content.Context
import androidx.annotation.IdRes
import androidx.annotation.NavigationRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.android.myapplication.newsfeed.R
import com.google.android.material.bottomnavigation.BottomNavigationView

/*
*
* reference https://github.com/mitchtabian/Open-API-Android-App/blob/Custom-Bottom-Nav-Controller/app/src/main/java/com/codingwithmitch/openapi/util/BottomNavController.kt
* */

class BottomNavController(
    val context: Context,
    @IdRes val containerId: Int,
    @IdRes val appStartDestinationId: Int,
    val graphChangeListener: OnNavigationGraphChanged?,
    val navGraphProvider: NavGraphProvider
    /*
    * Bottom navigation will have its own backStack
    * and each entry in this navigation will have its own backStack
    * so we have a total of 4 (bottom nav, headlines, sources, favorites)
    * */
) {

    lateinit var activity: Activity
    lateinit var fragmentManager: FragmentManager
    lateinit var navItemChangeListener: OnNavigationItemChanged
    private val navigationBackStack = BackStack.of(appStartDestinationId)

    init {
        if (context is Activity) {
            activity = context
            fragmentManager = (activity as FragmentActivity).supportFragmentManager
        }
    }

    fun onNavigationItemSelected(itemId: Int = navigationBackStack.last()): Boolean {
        val fragment = fragmentManager.findFragmentByTag(itemId.toString())
            ?: NavHostFragment.create(navGraphProvider.getNavGraphId(itemId))
        fragmentManager.beginTransaction().setCustomAnimations(
            R.anim.fade_in,
            R.anim.fade_out,
            R.anim.fade_in,
            R.anim.fade_out
        )
            .replace(containerId, fragment, itemId.toString())
            .addToBackStack(null)
            .commit()

        //Add to backstack
        navigationBackStack.moveLast(itemId)

        //update checked icon
        navItemChangeListener.onItemChanged(itemId)

        //communicate with Activity
        graphChangeListener?.onGraphChange()

        return true
    }

    fun onBackPressed(){
        val childFragmentManager = fragmentManager.findFragmentById(containerId)!!.childFragmentManager
        when{
            childFragmentManager.popBackStackImmediate()->{

            }
            //Fragment BackStack is empty, try to back on the navigation stack
            navigationBackStack.size > 1 -> {
                //Remove last item from backStack
                navigationBackStack.removeLast()
                onNavigationItemSelected()
            }
            //If the stack has only one and it's not the navigation home we
            //should ensure that the app always leave from start destination
            navigationBackStack.last() != appStartDestinationId -> {
                navigationBackStack.run {
                    removeLast()
                    add(0,appStartDestinationId)
                }
                onNavigationItemSelected()
            }
            else-> activity.finish()
        }
    }
    private class BackStack : ArrayList<Int>() {
        companion object {
            fun of(vararg elements: Int) = BackStack().apply { addAll(elements.toTypedArray()) }
        }

        fun removeLast() = removeAt(size - 1)
        fun moveLast(item: Int) {
            remove(item)
            add(item)
        }
    }

    //for setting the checked icon in the bottom nav
    interface OnNavigationItemChanged {
        fun onItemChanged(itemId: Int)

    }

    fun setOnItemNavigationChanged(listener: (itemId: Int) -> Unit) {
        this.navItemChangeListener = object : OnNavigationItemChanged {
            override fun onItemChanged(itemId: Int) {
                listener.invoke(itemId)
            }

        }
    }

    //Get id of each graph
    //ex: R.navigation.
    interface NavGraphProvider {
        @NavigationRes
        fun getNavGraphId(itemId: Int): Int
    }

    //Execute when navigation graph changes
    //ex: Select a new item on the bottom nav
    //ex: Headlines-> Sources
    interface OnNavigationGraphChanged {
        fun onGraphChange()
    }

    interface OnNavigationReselectedListener {
        fun onReselectNavItem(navController: NavController, fragment: Fragment)
    }

}

/*
* extension function
* */

fun BottomNavigationView.setUpNavigation(
    bottomNavController: BottomNavController,
    onReselectListener: BottomNavController.OnNavigationReselectedListener
) {
    setOnNavigationItemSelectedListener {
        bottomNavController.onNavigationItemSelected(it.itemId)
    }
    setOnNavigationItemReselectedListener {
        bottomNavController
            .fragmentManager
            .findFragmentById(bottomNavController.containerId)!!
            .childFragmentManager
            .fragments[0]?.let { fragment ->
            onReselectListener.onReselectNavItem(
                bottomNavController.activity.findNavController(
                    bottomNavController.containerId
                ),fragment
            )
        }
    }
    bottomNavController.setOnItemNavigationChanged { itemId->
        menu.findItem(itemId).isChecked = true
    }
}