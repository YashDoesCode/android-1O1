package com.YashDoesCode.calculator

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.YashDoesCode.calculator.ui.CalculatorScreen
import com.YashDoesCode.calculator.ui.theme.CalculatorTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CalculatorScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testCalculatorKeypadInputAndCalculation() {
        composeTestRule.setContent {
            CalculatorTheme {
                CalculatorScreen()
            }
        }

        // Tap 5 + 7 =
        composeTestRule.onNodeWithText("5").performClick()
        composeTestRule.onNodeWithText("+").performClick()
        composeTestRule.onNodeWithText("7").performClick()

        // Verify live expression display
        composeTestRule.onNodeWithTag("CalculatorExpressionDisplay").assertIsDisplayed()

        // Tap equals
        composeTestRule.onNodeWithText("=").performClick()

        // Verify result display
        composeTestRule.onNodeWithText("12").assertIsDisplayed()
    }

    @Test
    fun testScientificKeypadToggle() {
        composeTestRule.setContent {
            CalculatorTheme {
                CalculatorScreen()
            }
        }

        // Toggle scientific mode
        composeTestRule.onNodeWithTag("ScientificToggle").performClick()

        // Verify scientific functions are visible
        composeTestRule.onNodeWithText("sin").assertIsDisplayed()
        composeTestRule.onNodeWithText("cos").assertIsDisplayed()
        composeTestRule.onNodeWithText("π").assertIsDisplayed()
    }
}
