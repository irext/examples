package net.irext.ircontrol.compose.ui.composable

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

/**
 * Filename:       ItemComposableTest.kt
 * Created:        Date: 2026-07-09
 *
 * Description:    Tests reusable Compose list item components.
 *
 * Revision log:
 * 2026-07-09: created by shdmfire and strawmanbobi
 */

class ItemComposableTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun itemSingleText_displaysCorrectText() {
        composeTestRule.setContent {
            ItemSingleText(text = "Test Brand")
        }
        composeTestRule.onNodeWithText("Test Brand").assertExists()
    }

    @Test
    fun itemIndexText_displaysBothTexts() {
        composeTestRule.setContent {
            ItemIndexText(nameText = "Index 1", mapText = "Map Data")
        }
        composeTestRule.onNodeWithText("Index 1").assertExists()
        composeTestRule.onNodeWithText("Map Data").assertExists()
    }
}
