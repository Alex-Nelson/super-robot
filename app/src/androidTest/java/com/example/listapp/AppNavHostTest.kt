package com.example.listapp

import android.app.Application
import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.listapp.data.ListDatabase
import com.example.listapp.viewmodel.AppViewModel
import com.example.listapp.viewmodel.AppViewModelFactory
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * This file contains test for the navigation in compose.
 *
 * */
class AppNavHostTest {

    private lateinit var appDatabase: ListDatabase
    private lateinit var viewModel: AppViewModel

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val composeTestRule = createComposeRule()
    lateinit var navController: NavHostController

    @ExperimentalAnimationApi
    @ExperimentalComposeUiApi
    @Before
    fun setUp(){
        val context = ApplicationProvider.getApplicationContext<Context>()
        appDatabase = Room.inMemoryDatabaseBuilder(
            context, ListDatabase::class.java).build()

        val viewModelFactory = AppViewModelFactory(Application())
        viewModel = ViewModelProvider(ViewModelStore(), viewModelFactory)
            .get(AppViewModel::class.java)

        composeTestRule.setContent {
            navController = rememberNavController()

            ListAppNavHost(
                navController = navController,
                viewModel = viewModel
            )
        }
    }

    @After
    fun tearDown(){
        appDatabase.close()
    }

    @ExperimentalAnimationApi
    @ExperimentalComposeUiApi
    @Test
    fun appNavHost(){
        composeTestRule.setContent {
            navController = rememberNavController()

            ListAppNavHost(
                navController = navController,
                viewModel = viewModel
            )
        }
        composeTestRule
            .onNodeWithContentDescription("Home Screen")
            .assertIsDisplayed()
    }

    @Test
    fun listNavHost_navigateToManageList_viaUI(){
        composeTestRule
            .onNodeWithContentDescription("")
            .performClick()
    }
}