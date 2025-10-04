package com.YashDoesCode.browser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.YashDoesCode.browser.ui.FullBrowserScreen
import com.YashDoesCode.browser.ui.FullBrowserViewModel
import com.YashDoesCode.designsystem.theme.AppTheme

class MainActivity : ComponentActivity() {

    private val viewModel: FullBrowserViewModel by viewModels {
        val app = application as BrowserApp
        FullBrowserViewModel.Factory(app.container)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FullBrowserScreen(viewModel = viewModel)
                }
            }
        }
    }
}
