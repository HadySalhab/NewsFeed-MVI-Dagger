package com.android.myapplication.newsfeed.ui.headlines.viewmodel

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.android.myapplication.newsfeed.repository.HeadlinesRepository
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
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

    @Before
    fun setUp() {
        `when`(sharedPreferences.edit()).thenReturn(sharePreferencesEditor)

        SUT = HeadlinesViewModel(headlinesRepository,sharedPreferences,sharePreferencesEditor)
    }
    @Test
    fun saveCategoryAndCountry_shouldSaveToSharedPreferences(){

        SUT.saveCategoryAndCountry(COUNTRY,CATEGORY)

        verify(sharePreferencesEditor).putString(any(), eq(COUNTRY))
        verify(sharePreferencesEditor).putString(any(), eq(CATEGORY))
        verify(sharePreferencesEditor).apply()

    }
}
