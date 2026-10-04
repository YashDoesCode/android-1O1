package com.YashDoesCode.browser.lite.ui

data class LiteBrowserUiState(
    val url: String = "https://www.google.com",
    val title: String? = null,
    val isLoading: Boolean = false,
    val progress: Int = 0,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val isDataSaverEnabled: Boolean = true,
    val isBookmarked: Boolean = false,
    val showBookmarksDialog: Boolean = false,
    val showHistoryDialog: Boolean = false
)

sealed interface LiteBrowserAction {
    data class NavigateToUrl(val input: String) : LiteBrowserAction
    data object GoBack : LiteBrowserAction
    data object GoForward : LiteBrowserAction
    data object Reload : LiteBrowserAction
    data object Stop : LiteBrowserAction
    data object ToggleDataSaver : LiteBrowserAction
    data object ToggleBookmark : LiteBrowserAction
    data class ShowBookmarks(val show: Boolean) : LiteBrowserAction
    data class ShowHistory(val show: Boolean) : LiteBrowserAction
    data class UpdateProgress(
        val progress: Int,
        val title: String?,
        val url: String?,
        val canGoBack: Boolean,
        val canGoForward: Boolean
    ) : LiteBrowserAction
}
