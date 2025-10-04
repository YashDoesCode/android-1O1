package com.YashDoesCode.comms

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.YashDoesCode.comms.domain.model.EmailMessage
import com.YashDoesCode.comms.domain.model.EmailThread
import com.YashDoesCode.comms.ui.components.CommsBottomBar
import com.YashDoesCode.comms.ui.components.CommsTab
import com.YashDoesCode.comms.ui.dialer.DialerAction
import com.YashDoesCode.comms.ui.dialer.components.DialpadSection
import com.YashDoesCode.comms.ui.mail.components.InboxThreadList
import com.YashDoesCode.comms.ui.theme.CommsTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CommsUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testBottomNavSwitching() {
        var currentTab by mutableStateOf(CommsTab.CALLS)

        composeTestRule.setContent {
            CommsTheme {
                CommsBottomBar(
                    currentTab = currentTab,
                    onTabSelected = { currentTab = it }
                )
            }
        }

        composeTestRule.onNodeWithText("Calls").assertIsDisplayed()
        composeTestRule.onNodeWithText("Mail").assertIsDisplayed()

        // Tap Mail tab
        composeTestRule.onNodeWithText("Mail").performClick()
        assertEquals(CommsTab.MAIL, currentTab)

        // Tap Calls tab
        composeTestRule.onNodeWithText("Calls").performClick()
        assertEquals(CommsTab.CALLS, currentTab)
    }

    @Test
    fun testDialpadKeypadInput() {
        var digits by mutableStateOf("")
        var placedCallNumber: String? = null

        composeTestRule.setContent {
            CommsTheme {
                DialpadSection(
                    digits = digits,
                    onAction = { action ->
                        when (action) {
                            is DialerAction.AppendDigit -> digits += action.digit
                            is DialerAction.DeleteDigit -> if (digits.isNotEmpty()) digits = digits.dropLast(1)
                            is DialerAction.ClearDigits -> digits = ""
                            else -> Unit
                        }
                    },
                    onCallClick = { placedCallNumber = it }
                )
            }
        }

        // Tap 1, 2, 3
        composeTestRule.onNodeWithText("1").performClick()
        composeTestRule.onNodeWithText("2").performClick()
        composeTestRule.onNodeWithText("3").performClick()
        assertEquals("123", digits)

        // Tap Place Call
        composeTestRule.onNodeWithContentDescription("Place Call").performClick()
        assertEquals("123", placedCallNumber)
    }

    @Test
    fun testInboxThreadListRendersThreads() {
        val sampleThreads = listOf(
            EmailThread(
                id = "thread-1",
                senderName = "GitHub Notifications",
                senderEmail = "notifications@github.com",
                subject = "New Release v1.0.0",
                snippet = "A new release has been published for android-1O1.",
                lastMessageTimestamp = System.currentTimeMillis(),
                unreadCount = 1,
                isStarred = true,
                messages = listOf(
                    EmailMessage(
                        id = "msg-1",
                        threadId = "thread-1",
                        senderName = "GitHub Notifications",
                        senderEmail = "notifications@github.com",
                        recipientEmail = "me@example.com",
                        subject = "New Release v1.0.0",
                        body = "A new release has been published for android-1O1.",
                        timestamp = System.currentTimeMillis()
                    )
                )
            )
        )

        composeTestRule.setContent {
            CommsTheme {
                InboxThreadList(
                    threads = sampleThreads,
                    searchQuery = "",
                    filterUnreadOnly = false,
                    filterStarredOnly = false,
                    onAction = {}
                )
            }
        }

        composeTestRule.onNodeWithText("GitHub Notifications").assertIsDisplayed()
        composeTestRule.onNodeWithText("New Release v1.0.0").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Compose Email").assertIsDisplayed()
    }
}
