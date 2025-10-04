package com.YashDoesCode.browser.ui

import com.YashDoesCode.browser.core.domain.model.Bookmark
import com.YashDoesCode.browser.core.domain.model.HistoryEntry
import com.YashDoesCode.browser.core.domain.model.Tab

data class FullBrowserUiState(
    val tabs: List<Tab> = listOf(Tab(id = "tab_1", url = "https://www.google.com")),
    val activeTabId: String = "tab_1",
    val isBookmarked: Boolean = false,
    val showBookmarksDialog: Boolean = false,
    val showHistoryDialog: Boolean = false,
    val showTabsOverview: Boolean = false,
    val downloadNotification: String? = null
) {
    val activeTab: Tab
        get() = tabs.find { it.id == activeTabId } ?: tabs.first()
}

sealed interface FullBrowserAction {
    data class NavigateToUrl(val input: String) : FullBrowserAction
    data class NewTab(val url: String = "https://www.google.com") : FullBrowserAction
    data class SwitchTab(val tabId: String) : FullBrowserAction
    data class CloseTab(val tabId: String) : FullBrowserAction
    data object GoBack : FullBrowserAction
    data object GoForward : FullBrowserAction
    data object Reload : FullBrowserAction
    data object Stop : FullBrowserAction
    data object ToggleDesktopMode : FullBrowserAction
    data object ToggleBookmark : FullBrowserAction
    data class ShowBookmarks(val show: Boolean) : FullBrowserAction
    data class ShowHistory(val show: Boolean) : FullBrowserAction
    data class ShowTabsOverview(val show: Boolean) : FullBrowserAction
    data class UpdateTabProgress(
        val tabId: String,
        val progress: Int,
        val title: String?,
        val url: String?,
        val canGoBack: Boolean,
        val canGoForward: Boolean
    ) : FullBrowserAction
}
