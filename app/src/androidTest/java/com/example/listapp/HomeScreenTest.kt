package com.example.listapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.listapp.home.EmptyListHome

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Testing the UI for the Home Screen
 *
 * */
@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Test the empty state displays
     * */
//    @Test
//    fun homeScreen_emptyStateDisplayed(){
//        composeTestRule.setContent {
//            EmptyListHome()
//        }
//
//        composeTestRule
//            .onNodeWithText("")
//            .assertIsDisplayed()
//    }
}