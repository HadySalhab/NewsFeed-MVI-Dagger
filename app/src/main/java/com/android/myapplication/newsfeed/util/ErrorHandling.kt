package com.android.myapplication.newsfeed.util

class ErrorHandling {
    companion object{
        const val UNABLE_TODO_OPERATION_WO_INTERNET = "Can't do that operation without an internet connection"
        const val UNABLE_TO_RESOLVE_HOST = "Unable to resolve host"
        const val ERROR_UNKNOWN = "Unknown error"
        const val ERROR_CHECK_NETWORK_CONNECTION = "Check network connection."

        fun isNetworkError(msg: String): Boolean{
            when{
                msg.contains(UNABLE_TO_RESOLVE_HOST) -> return true
                else-> return false
            }
        }
    }


}