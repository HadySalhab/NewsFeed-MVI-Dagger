package com.android.myapplication.newsfeed.ui

class DataState<T> private constructor(
 var error: Event<StateError>? = null,
 var loading: Loading = Loading(
  false
 ),
 var data: Data<T>? = null
) {

 //CONSIDER STATIC
 //FACTORY METHODS INSTEAD OF
 //CONSTRUCTORS
 //item 1
 companion object {

  fun <T> error(
   response: Response
  ): DataState<T> = DataState(
    error = Event(
     StateError(
      response
     )
    )
   )

  fun <T> loading(
   isLoading: Boolean
  ): DataState<T> = DataState(
    loading = Loading(isLoading)
   )

  fun <T> data(
   data: T? = null,
   response: Response? = null
  ) = DataState(
    data = Data(
     Event.dataEvent(data),
     Event.responseEvent(response)
    )
   )
  fun <T> none():DataState<T> = DataState()
  }
 }