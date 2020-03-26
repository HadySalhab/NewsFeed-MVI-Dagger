package com.android.myapplication.newsfeed.ui.headlines.viewmodel

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.android.myapplication.newsfeed.models.Article
import com.android.myapplication.newsfeed.repository.HeadlinesRepository
import com.android.myapplication.newsfeed.ui.DataState
import com.android.myapplication.newsfeed.ui.headlines.state.HeadlinesStateEvent
import com.android.myapplication.newsfeed.ui.headlines.state.HeadlinesViewState
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import org.hamcrest.core.Is.`is`
import org.hamcrest.core.IsInstanceOf.instanceOf
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class HeadlinesViewModelTest{
    val COUNTRY = "Australia"
    val CATEGORY = "Entertainment"
    val SEARCH_QUERY = ""
    val PAGE = 1

    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    private lateinit var SUT:HeadlinesViewModel
    @Mock
    private lateinit var sharedPreferences: SharedPreferences
    @Mock
    private lateinit var sharePreferencesEditor: SharedPreferences.Editor
    @Mock
    private lateinit var headlinesRepository: HeadlinesRepository

    @Mock
    private lateinit var headlinesStateEventObserver: Observer<HeadlinesStateEvent>

    @Mock
    private lateinit var headlinesDataStateObserver: Observer<DataState<HeadlinesViewState>>

    @Before
    fun setUp() {
        //`when`(sharedPreferences.edit()).thenReturn(sharePreferencesEditor)

        SUT = HeadlinesViewModel(headlinesRepository,sharedPreferences,sharePreferencesEditor)

        SUT.stateEvent.observeForever(headlinesStateEventObserver)
        SUT.dataState.observeForever(headlinesDataStateObserver)
    }
    @Test
    fun saveCategoryAndCountry_shouldSaveToSharedPreferences(){

        SUT.saveCategoryAndCountry(COUNTRY,CATEGORY)

        verify(sharePreferencesEditor).putString(any(), eq(COUNTRY))
        verify(sharePreferencesEditor).putString(any(), eq(CATEGORY))
        verify(sharePreferencesEditor).apply()

    }
    @Test
    fun cancelActiveJob_shouldCallRepositoryCancelActiveJob(){

        SUT.cancelActiveJobs()

        verify(headlinesRepository).cancelActiveJobs()
    }

    @Test
    fun setStateEvent_None_shouldSetStateEventLiveDataToNone(){
        val ac = ArgumentCaptor.forClass(HeadlinesStateEvent::class.java)

        SUT.setStateEvent(HeadlinesStateEvent.None())

        verify(headlinesStateEventObserver).onChanged(ac.capture())
        Assert.assertThat(ac.getValue(),`is`(instanceOf(HeadlinesStateEvent.None::class.java)))
    }

    @Test
    fun setStateEvent_SearchEvent_shouldSetStateEventLiveDataToSearchEvent(){
        val ac = ArgumentCaptor.forClass(HeadlinesStateEvent::class.java)

        SUT.setStateEvent(HeadlinesStateEvent.HeadlinesSearchEvent("","","",1))

        verify(headlinesStateEventObserver).onChanged(ac.capture())
        Assert.assertThat(ac.getValue(),`is`(instanceOf(HeadlinesStateEvent.HeadlinesSearchEvent::class.java)))
    }

    @Test
    fun setStateEvent_AddToFavEvent_shouldSetStateEventLiveDataToAddToFavEvent(){
        val ac = ArgumentCaptor.forClass(HeadlinesStateEvent::class.java)
        val article = Article()

        SUT.setStateEvent(HeadlinesStateEvent.HeadlinesAddToFavEvent(article))

        verify(headlinesStateEventObserver).onChanged(ac.capture())
        Assert.assertThat(ac.getValue(),`is`(instanceOf(HeadlinesStateEvent.HeadlinesAddToFavEvent::class.java)))
    }

    @Test
    fun setStateEvent_CheckFavEvent_shouldSetStateEventLiveDataToCheckFavEvent(){
        val ac = ArgumentCaptor.forClass(HeadlinesStateEvent::class.java)

        SUT.setStateEvent(HeadlinesStateEvent.HeadlinesCheckFavEvent(emptyList(),false))

        verify(headlinesStateEventObserver).onChanged(ac.capture())
        Assert.assertThat(ac.getValue(),`is`(instanceOf(HeadlinesStateEvent.HeadlinesCheckFavEvent::class.java)))
    }


    @Test
    fun setStateEvent_RemoveFromFavEvent_shouldSetStateEventLiveDataToRemoveFromFavEvent(){
        val ac = ArgumentCaptor.forClass(HeadlinesStateEvent::class.java)

        SUT.setStateEvent(HeadlinesStateEvent.HeadlinesRemoveFromFavEvent(Article()))

        verify(headlinesStateEventObserver).onChanged(ac.capture())
        Assert.assertThat(ac.getValue(),`is`(instanceOf(HeadlinesStateEvent.HeadlinesRemoveFromFavEvent::class.java)))
    }

    //Handle State Event
    @Test
    fun handleStateEvent_HeadlinesSearchEvent_ShouldCallRepositoryGetTopHeadlines(){
        val stateEvent = HeadlinesStateEvent.HeadlinesSearchEvent(COUNTRY,CATEGORY,SEARCH_QUERY,PAGE)

        SUT.handleStateEvent(stateEvent)

        verify(headlinesRepository).getTopHeadlines(eq(COUNTRY),eq(CATEGORY),eq(SEARCH_QUERY),eq(PAGE))
    }

    @Test
    fun handleStateEvent_HeadlinesSearchEvent_returnCorrectValue(){
        val fakeLiveData = object:LiveData<DataState<HeadlinesViewState>>(){
            override fun onActive() {
                super.onActive()
                value = DataState.none()
            }
        }
        whenever(headlinesRepository.getTopHeadlines(COUNTRY,CATEGORY,SEARCH_QUERY,PAGE)).thenReturn(fakeLiveData)
        val stateEvent = HeadlinesStateEvent.HeadlinesSearchEvent(COUNTRY,CATEGORY,SEARCH_QUERY,PAGE)


       val result =  SUT.handleStateEvent(stateEvent)


        Assert.assertEquals(fakeLiveData,result)
    }

    @Test
    fun handleStateEvent_HeadlinesAddToFavEvent_ShouldCallRepositoryInsertArticleToDB(){
        val article = Article()
        val stateEvent = HeadlinesStateEvent.HeadlinesAddToFavEvent(article)

        SUT.handleStateEvent(stateEvent)

        verify(headlinesRepository).insertArticleToDB(eq(article))
    }

    @Test
    fun handleStateEvent_HeadlinesAddToFavEvent_ShouldReturnCorrectValue(){
        val article = Article()
        val fakeLiveData = object:LiveData<DataState<HeadlinesViewState>>(){
            override fun onActive() {
                super.onActive()
                value = DataState.none()
            }
        }
        whenever(headlinesRepository.insertArticleToDB(article)).thenReturn(fakeLiveData)
        val stateEvent = HeadlinesStateEvent.HeadlinesAddToFavEvent(article)


        val result =  SUT.handleStateEvent(stateEvent)


        Assert.assertEquals(fakeLiveData,result)
    }

    @Test
    fun handleStateEvent_HeadlinesRemoveFromFavEvent_shouldCallRepositoryDeleteArticleFromDB(){
        val article = Article()

        SUT.handleStateEvent(HeadlinesStateEvent.HeadlinesRemoveFromFavEvent(article))

        verify(headlinesRepository).deleteArticleFromDB(eq(article))
    }
    @Test
    fun handleStateEvent_HeadlinesRemoveFromFavEvent_ShouldReturnCorrectValue(){
        val article = Article()
        val fakeLiveData = object:LiveData<DataState<HeadlinesViewState>>(){
            override fun onActive() {
                super.onActive()
                value = DataState.none()
            }
        }
        whenever(headlinesRepository.deleteArticleFromDB(article)).thenReturn(fakeLiveData)
        val stateEvent = HeadlinesStateEvent.HeadlinesRemoveFromFavEvent(article)


        val result =  SUT.handleStateEvent(stateEvent)


        Assert.assertEquals(fakeLiveData,result)
    }

    @Test
    fun handleStateEvent_HeadlinesCheckFavEvent_shouldCallRepositoryCheckFavorite(){
        val article = Article()
        val listOfArticles = listOf(article)
        val isQueryExhausted = false

        SUT.handleStateEvent(HeadlinesStateEvent.HeadlinesCheckFavEvent(listOfArticles, isQueryExhausted))

        verify(headlinesRepository).checkFavorite(eq(listOfArticles), eq(isQueryExhausted))
    }

    @Test
    fun handleStateEvent_HeadlinesCheckFavEvent_ShouldReturnCorrectValue(){
        val article = Article()
        val listOfArticles = listOf(article)
        val isQueryExhausted = false
        val fakeLiveData = object:LiveData<DataState<HeadlinesViewState>>(){
            override fun onActive() {
                super.onActive()
                value = DataState.none()
            }
        }
        whenever(headlinesRepository.checkFavorite(listOfArticles,isQueryExhausted)).thenReturn(fakeLiveData)
        val stateEvent = HeadlinesStateEvent.HeadlinesCheckFavEvent(listOfArticles,isQueryExhausted)


        val result =  SUT.handleStateEvent(stateEvent)


        Assert.assertEquals(fakeLiveData,result)
    }

    @Test
    fun cancelActiveJob_shouldSetStateEventToNone(){
        val ac = ArgumentCaptor.forClass(HeadlinesStateEvent::class.java)

        SUT.cancelActiveJobs()

        verify(headlinesStateEventObserver).onChanged(ac.capture())
        Assert.assertThat(ac.getValue(),`is`(instanceOf(HeadlinesStateEvent.None::class.java)))
    }







}
