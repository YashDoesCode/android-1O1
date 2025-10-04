package com.YashDoesCode.browser.lite.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import com.YashDoesCode.browser.core.ui.components.BookmarksDialog
import com.YashDoesCode.browser.core.ui.components.HistoryDialog

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LiteBrowserScreen(
    viewModel: LiteBrowserViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()
    val history by viewModel.history.collectAsState()

    var textInput by remember(uiState.url) { mutableStateOf(uiState.url) }
    val focusManager = LocalFocusManager.current
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    tonalElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Back
                        IconButton(
                            onClick = { viewModel.onAction(LiteBrowserAction.GoBack) },
                            enabled = uiState.canGoBack
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }

                        // Forward
                        IconButton(
                            onClick = { viewModel.onAction(LiteBrowserAction.GoForward) },
                            enabled = uiState.canGoForward
                        ) {
                            Icon(Icons.Default.ArrowForward, contentDescription = "Forward")
                        }

                        // URL Input
                        TextField(
                            value = textInput,
                            onValueChange = { textInput = it },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp),
                            singleLine = true,
                            placeholder = { Text("Lite Search or URL") },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                            keyboardActions = KeyboardActions(
                                onGo = {
                                    focusManager.clearFocus()
                                    viewModel.onAction(LiteBrowserAction.NavigateToUrl(textInput))
                                }
                            ),
                            trailingIcon = {
                                IconButton(onClick = { viewModel.onAction(LiteBrowserAction.ToggleBookmark) }) {
                                    Icon(
                                        imageVector = if (uiState.isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                                        contentDescription = "Bookmark",
                                        tint = if (uiState.isBookmarked) MaterialTheme.colorScheme.primary else LocalContentColor.current
                                    )
                                }
                            },
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                                unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
                            )
                        )

                        // Data Saver Toggle (Speed / Image Block)
                        IconButton(onClick = { viewModel.onAction(LiteBrowserAction.ToggleDataSaver) }) {
                            Icon(
                                imageVector = Icons.Outlined.Speed,
                                contentDescription = "Data Saver",
                                tint = if (uiState.isDataSaverEnabled) MaterialTheme.colorScheme.primary else LocalContentColor.current
                            )
                        }

                        // Menu
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            if (uiState.isDataSaverEnabled) "Data Saver: ON (Images Blocked)" else "Data Saver: OFF"
                                        )
                                    },
                                    onClick = {
                                        showMenu = false
                                        viewModel.onAction(LiteBrowserAction.ToggleDataSaver)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Bookmarks") },
                                    leadingIcon = { Icon(Icons.Default.Bookmarks, contentDescription = null) },
                                    onClick = {
                                        showMenu = false
                                        viewModel.onAction(LiteBrowserAction.ShowBookmarks(true))
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("History") },
                                    leadingIcon = { Icon(Icons.Default.History, contentDescription = null) },
                                    onClick = {
                                        showMenu = false
                                        viewModel.onAction(LiteBrowserAction.ShowHistory(true))
                                    }
                                )
                            }
                        }
                    }
                }

                AnimatedVisibility(visible = uiState.isLoading) {
                    LinearProgressIndicator(
                        progress = { uiState.progress / 100f },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            allowFileAccess = false
                            allowContentAccess = false
                        }
                        CookieManager.getInstance().setAcceptCookie(true)
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { webView ->
                    // Apply Data Saver setting
                    webView.settings.blockNetworkImage = uiState.isDataSaverEnabled

                    webView.webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            super.onPageStarted(view, url, favicon)
                            viewModel.onAction(
                                LiteBrowserAction.UpdateProgress(
                                    progress = 10,
                                    title = view?.title,
                                    url = url,
                                    canGoBack = view?.canGoBack() == true,
                                    canGoForward = view?.canGoForward() == true
                                )
                            )
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            viewModel.onAction(
                                LiteBrowserAction.UpdateProgress(
                                    progress = 100,
                                    title = view?.title,
                                    url = url,
                                    canGoBack = view?.canGoBack() == true,
                                    canGoForward = view?.canGoForward() == true
                                )
                            )
                        }

                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            val url = request?.url?.toString() ?: return false
                            return !(url.startsWith("http://") || url.startsWith("https://"))
                        }
                    }

                    webView.webChromeClient = object : WebChromeClient() {
                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                            super.onProgressChanged(view, newProgress)
                            viewModel.onAction(
                                LiteBrowserAction.UpdateProgress(
                                    progress = newProgress,
                                    title = view?.title,
                                    url = view?.url,
                                    canGoBack = view?.canGoBack() == true,
                                    canGoForward = view?.canGoForward() == true
                                )
                            )
                        }
                    }

                    if (webView.url != uiState.url && uiState.url.isNotEmpty()) {
                        webView.loadUrl(uiState.url)
                    }
                }
            )
        }
    }

    if (uiState.showBookmarksDialog) {
        BookmarksDialog(
            bookmarks = bookmarks,
            onSelectBookmark = { url ->
                viewModel.onAction(LiteBrowserAction.NavigateToUrl(url))
                viewModel.onAction(LiteBrowserAction.ShowBookmarks(false))
            },
            onDeleteBookmark = { url ->
                viewModel.deleteBookmark(url)
            },
            onDismiss = {
                viewModel.onAction(LiteBrowserAction.ShowBookmarks(false))
            }
        )
    }

    if (uiState.showHistoryDialog) {
        HistoryDialog(
            history = history,
            onSelectHistory = { url ->
                viewModel.onAction(LiteBrowserAction.NavigateToUrl(url))
                viewModel.onAction(LiteBrowserAction.ShowHistory(false))
            },
            onClearHistory = {
                viewModel.clearHistory()
            },
            onDismiss = {
                viewModel.onAction(LiteBrowserAction.ShowHistory(false))
            }
        )
    }
}
