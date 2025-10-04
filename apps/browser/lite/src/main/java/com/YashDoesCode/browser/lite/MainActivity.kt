package com.YashDoesCode.browser.lite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.YashDoesCode.browser.lite.ui.LiteBrowserScreen
import com.YashDoesCode.browser.lite.ui.LiteBrowserViewModel
import com.YashDoesCode.designsystem.theme.AppTheme

class MainActivity : ComponentActivity() {

    private val viewModel: LiteBrowserViewModel by viewModels {
        val app = application as LiteBrowserApp
        LiteBrowserViewModel.Factory(app.container)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LiteBrowserScreen(viewModel = viewModel)
                }
            }
        }
    }
}
