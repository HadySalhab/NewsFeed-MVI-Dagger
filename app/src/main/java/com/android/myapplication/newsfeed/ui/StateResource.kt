package com.android.myapplication.newsfeed.ui

import com.android.myapplication.newsfeed.util.ERROR_UNKNOWN


class Loading(val isLoading: Boolean)
class Data<T>(val data: Event<T>?, val response: Event<Response>?)
class StateError(val response: Response)


class Response private constructor(val message: String?, val responseType: ResponseType){
    companion object{
        fun dialogResponse(message: String = ERROR_UNKNOWN):Response = Response(message,ResponseType.Dialog())
        fun toastResponse(message:String= ERROR_UNKNOWN):Response = Response(message,ResponseType.Toast())
    }
}
sealed class ResponseType {

    class Toast : ResponseType()

    class Dialog : ResponseType()

    class None : ResponseType()
}


/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 */
open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again,WITHIN THE SAME OBJECT.
     */
    fun getContentIfNotHandled() = if (hasBeenHandled) {
        null
    } else {
        hasBeenHandled = true
        content
    }


    /**
     * Returns the content, even if it's already been handled.
     * calling peekContent before getContentIfNotHandled will not mark it as it has been handled within the same object
     * so event calling `getContentIfNotHandled` after peekContent is still not handled
     */
    fun peekContent(): T = content

    override fun toString() = "Event(content=$content, hasBeenHandled=$hasBeenHandled)"


    companion object {
        // we don't want an event if the data is null
        fun <T> dataEvent(data: T?)= if(data!=null) Event(data) else null

        // we don't want an event if the response is null
        fun responseEvent(response: Response?) = if (response != null) Event(response) else null
    }


}