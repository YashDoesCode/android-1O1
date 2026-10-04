package com.YashDoesCode.browser.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import com.YashDoesCode.browser.core.di.BrowserCoreContainer
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FullBrowserUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: FullBrowserViewModel

    @Before
    fun setUp() {
        val container = BrowserCoreContainer(ApplicationProvider.getApplicationContext())
        viewModel = FullBrowserViewModel(container)
    }

    @Test
    fun omniboxDisplaysSearchPlaceholderAndTabsCount() {
        composeTestRule.setContent {
            FullBrowserScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithText("Search or type URL").assertExists()
        composeTestRule.onNodeWithText("1").assertExists()
    }

    @Test
    fun openingOverflowMenuDisplaysOptions() {
        composeTestRule.setContent {
            FullBrowserScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithContentDescription("Menu").performClick()
        composeTestRule.onNodeWithText("New Tab").assertExists()
        composeTestRule.onNodeWithText("Bookmarks").assertExists()
        composeTestRule.onNodeWithText("History").assertExists()
    }
}
