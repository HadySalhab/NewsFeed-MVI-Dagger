package com.android.myapplication.newsfeed.ui

import android.content.Context
import android.util.Log
import android.view.inputmethod.InputMethodManager
import com.android.myapplication.newsfeed.util.TAG
import com.android.myapplication.newsfeed.util.displayErrorDialog
import com.android.myapplication.newsfeed.util.displayToast
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

abstract class BaseActivity:DaggerAppCompatActivity(),DataStateChangeListener{
    //When dataState is changed, we BaseActivity handles:
    //loading state, error state, *response* of data(success) state
    //data of data is handled in the activity/fragment itself
    override fun onDataStateChange(dataState: DataState<*>?) {
        dataState?.run{
            GlobalScope.launch(Dispatchers.Main){
                //displayProgressBar if loading
                displayProgressBar(loading.isLoading)

                error?.run {
                    //handle the error if not null
                     getContentIfNotHandled()?.run {
                        with(response){
                            handleStateResponseType(responseType,message)
                        }
                    }

                }

                //handle data if not null
                data?.run {
                    //handle response data if not null
                      response?.run {
                        getContentIfNotHandled()?.run {
                            handleStateResponseType(responseType,message)
                        }
                    }
                }
            }
        }
    }

    //we need access to the progress_Bar
    abstract fun displayProgressBar(bool: Boolean)

    private fun handleStateResponseType(responseType: ResponseType,message:String?){
        when(responseType){
            is ResponseType.Toast ->{
             message?.run{
                    displayToast(this)
                }
            }

            is ResponseType.Dialog ->{
                    message?.run{
                    displayErrorDialog(this)
                }
            }

            is ResponseType.None -> {
                Log.i(TAG, "handleStateError: $message")
            }
        }
    }

    override fun hideSoftKeyboard() {
        currentFocus?.let {
            val inputMethodManager = getSystemService(
                Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager
                .hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }
}