package com.android.myapplication.newsfeed.ui.headlines.viewmodel

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.android.myapplication.newsfeed.repository.HeadlinesRepository
import com.android.myapplication.newsfeed.ui.headlines.state.HeadlinesStateEvent
import org.hamcrest.core.Is.`is`
import org.hamcrest.core.IsInstanceOf.instanceOf
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class HeadlinesViewModelTest{
    val COUNTRY = "Australia"
    val CATEGORY = "Entertainment"

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
    @Before
    fun setUp() {
        `when`(sharedPreferences.edit()).thenReturn(sharePreferencesEditor)

        SUT = HeadlinesViewModel(headlinesRepository,sharedPreferences,sharePreferencesEditor)

        SUT.stateEvent.observeForever(headlinesStateEventObserver)
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
    fun cancelActiveJob_shouldSetStateEventToNone(){
        val ac = ArgumentCaptor.forClass(HeadlinesStateEvent::class.java)

        SUT.cancelActiveJobs()

        verify(headlinesStateEventObserver).onChanged(ac.capture())
        Assert.assertThat(ac.getValue(),`is`(instanceOf(HeadlinesStateEvent.None::class.java)))
    }
}
