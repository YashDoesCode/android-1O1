package com.YashDoesCode.gallery

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.YashDoesCode.gallery.domain.model.Album
import com.YashDoesCode.gallery.ui.components.AlbumsGridScreen
import com.YashDoesCode.gallery.ui.theme.GalleryTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GalleryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testAlbumsGridScreenRendersAlbums() {
        val testAlbums = listOf(
            Album("camera", "Camera", null, 12),
            Album("screenshots", "Screenshots", null, 5)
        )
        var selectedAlbum: Album? = null

        composeTestRule.setContent {
            GalleryTheme {
                AlbumsGridScreen(
                    albums = testAlbums,
                    onSelectAlbum = { selectedAlbum = it }
                )
            }
        }

        composeTestRule.onNodeWithTag("AlbumsGrid").assertIsDisplayed()
        composeTestRule.onNodeWithText("Camera").assertIsDisplayed()
        composeTestRule.onNodeWithText("12 photos").assertIsDisplayed()
        composeTestRule.onNodeWithText("Screenshots").assertIsDisplayed()
        composeTestRule.onNodeWithText("5 photos").assertIsDisplayed()

        // Tap album
        composeTestRule.onNodeWithText("Camera").performClick()
        assert(selectedAlbum?.id == "camera")
    }
}
