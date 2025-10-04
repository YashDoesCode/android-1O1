package com.YashDoesCode.browser.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.YashDoesCode.browser.core.ui.components.BookmarksDialog
import com.YashDoesCode.browser.core.ui.components.HistoryDialog
import com.YashDoesCode.browser.ui.components.OmniboxBar
import com.YashDoesCode.browser.ui.components.TabsOverviewDialog
import com.YashDoesCode.browser.ui.components.WebViewContainer

@Composable
fun FullBrowserScreen(
    viewModel: FullBrowserViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()
    val history by viewModel.history.collectAsState()

    Scaffold(
        topBar = {
            OmniboxBar(
                activeTab = uiState.activeTab,
                tabsCount = uiState.tabs.size,
                isBookmarked = uiState.isBookmarked,
                onAction = viewModel::onAction
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            WebViewContainer(
                activeTab = uiState.activeTab,
                onAction = viewModel::onAction,
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    if (uiState.showBookmarksDialog) {
        BookmarksDialog(
            bookmarks = bookmarks,
            onSelectBookmark = { url ->
                viewModel.onAction(FullBrowserAction.NavigateToUrl(url))
                viewModel.onAction(FullBrowserAction.ShowBookmarks(false))
            },
            onDeleteBookmark = { url ->
                viewModel.deleteBookmark(url)
            },
            onDismiss = {
                viewModel.onAction(FullBrowserAction.ShowBookmarks(false))
            }
        )
    }

    if (uiState.showHistoryDialog) {
        HistoryDialog(
            history = history,
            onSelectHistory = { url ->
                viewModel.onAction(FullBrowserAction.NavigateToUrl(url))
                viewModel.onAction(FullBrowserAction.ShowHistory(false))
            },
            onClearHistory = {
                viewModel.clearHistory()
            },
            onDismiss = {
                viewModel.onAction(FullBrowserAction.ShowHistory(false))
            }
        )
    }

    if (uiState.showTabsOverview) {
        TabsOverviewDialog(
            tabs = uiState.tabs,
            activeTabId = uiState.activeTabId,
            onAction = viewModel::onAction,
            onDismiss = {
                viewModel.onAction(FullBrowserAction.ShowTabsOverview(false))
            }
        )
    }
}
