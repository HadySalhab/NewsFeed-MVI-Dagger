package com.android.myapplication.newsfeed.ui

import android.content.Context
import android.util.Log
import android.view.inputmethod.InputMethodManager
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

abstract class BaseActivity:DaggerAppCompatActivity(),DataStateChangeListener{
    val TAG: String = "AppDebug"
    //When dataState is changed, we BaseActivity handles:
    //loading state, error state, *response* of data(success) state
    //data of data is handled in the activity/fragment itself
    override fun onDataStateChange(dataState: DataState<*>?) {
        dataState?.let{
            GlobalScope.launch(Dispatchers.Main){
                //displayProgressBar if loading
                displayProgressBar(it.loading.isLoading)

                it.error?.let { errorEvent ->
                    //handle the error if not null
                    handleStateError(errorEvent)
                }

                //handle data if not null
                it.data?.let {
                    //handle response data if not null
                    it.response?.let { responseEvent ->
                        handleStateResponse(responseEvent)
                    }
                }
            }
        }
    }

    abstract fun displayProgressBar(bool: Boolean) //we need access to the progress_Bar

    private fun handleStateResponse(event: Event<Response>){
        event.getContentIfNotHandled()?.let{

            when(it.responseType){
                is ResponseType.Toast ->{
                    it.message?.let{message ->
                        displayToast(message)
                    }
                }

                is ResponseType.Dialog ->{
                    it.message?.let{ message ->
                        displaySuccessDialog(message)
                    }
                }

                is ResponseType.None -> {
                    Log.i(TAG, "handleStateResponse: ${it.message}")
                }
            }

        }
    }
    private fun handleStateError(event: Event<StateError>){
        event.getContentIfNotHandled()?.let{
            when(it.response.responseType){
                is ResponseType.Toast ->{
                    it.response.message?.let{message ->
                        displayToast(message)
                    }
                }

                is ResponseType.Dialog ->{
                    it.response.message?.let{ message ->
                        displayErrorDialog(message)
                    }
                }

                is ResponseType.None -> {
                    Log.i(TAG, "handleStateError: ${it.response.message}")
                }
            }
        }
    }

    override fun hideSoftKeyboard() {
        if (currentFocus != null) {
            val inputMethodManager = getSystemService(
                Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager
                .hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }
}