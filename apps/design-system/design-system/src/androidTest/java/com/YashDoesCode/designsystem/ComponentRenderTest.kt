package com.YashDoesCode.designsystem

import androidx.compose.material3.Text
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.YashDoesCode.designsystem.components.*
import com.YashDoesCode.designsystem.theme.AppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ComponentRenderTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun primaryButton_rendersAndHandlesClicks() {
        var clicked = false
        composeTestRule.setContent {
            AppTheme {
                PrimaryButton(
                    text = "Submit Action",
                    onClick = { clicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Submit Action").assertIsDisplayed().performClick()
        assert(clicked)
    }

    @Test
    fun appCard_rendersContent() {
        composeTestRule.setContent {
            AppTheme {
                AppCard {
                    Text("Card Content Inside")
                }
            }
        }

        composeTestRule.onNodeWithText("Card Content Inside").assertIsDisplayed()
    }

    @Test
    fun sectionHeader_rendersTitleAndAction() {
        var actionClicked = false
        composeTestRule.setContent {
            AppTheme {
                SectionHeader(
                    title = "Recent Items",
                    subtitle = "Last 7 days",
                    actionText = "See All",
                    onActionClick = { actionClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Recent Items").assertIsDisplayed()
        composeTestRule.onNodeWithText("Last 7 days").assertIsDisplayed()
        composeTestRule.onNodeWithText("See All").assertIsDisplayed().performClick()
        assert(actionClicked)
    }

    @Test
    fun stateScaffolds_renderCorrectly() {
        var retried = false
        composeTestRule.setContent {
            AppTheme {
                LoadingState(message = "Fetching data...")
            }
        }
        composeTestRule.onNodeWithText("Fetching data...").assertIsDisplayed()

        composeTestRule.setContent {
            AppTheme {
                EmptyState(
                    title = "No Records Found",
                    message = "Please check back later",
                    actionText = "Create New",
                    onActionClick = {}
                )
            }
        }
        composeTestRule.onNodeWithText("No Records Found").assertIsDisplayed()
        composeTestRule.onNodeWithText("Please check back later").assertIsDisplayed()
        composeTestRule.onNodeWithText("Create New").assertIsDisplayed()

        composeTestRule.setContent {
            AppTheme {
                ErrorState(
                    title = "Network Failure",
                    message = "Unable to reach server",
                    retryText = "Try Again",
                    onRetry = { retried = true }
                )
            }
        }
        composeTestRule.onNodeWithText("Network Failure").assertIsDisplayed()
        composeTestRule.onNodeWithText("Unable to reach server").assertIsDisplayed()
        composeTestRule.onNodeWithText("Try Again").assertIsDisplayed().performClick()
        assert(retried)
    }
}
