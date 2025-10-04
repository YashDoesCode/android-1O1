package com.YashDoesCode.gallery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.YashDoesCode.gallery.ui.GalleryScreen
import com.YashDoesCode.gallery.ui.GalleryViewModel
import com.YashDoesCode.gallery.ui.theme.GalleryTheme

class MainActivity : ComponentActivity() {

    private val viewModel: GalleryViewModel by viewModels {
        val app = application as GalleryApp
        GalleryViewModel.Factory(
            app.container.getAlbumsUseCase,
            app.container.getImagesInAlbumUseCase,
            app.container.getPagedImagesUseCase,
            app.container.getImageDetailsUseCase
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GalleryTheme {
                GalleryScreen(viewModel = viewModel)
            }
        }
    }
}
