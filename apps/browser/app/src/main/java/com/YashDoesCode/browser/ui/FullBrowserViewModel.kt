package com.YashDoesCode.browser.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.YashDoesCode.browser.core.di.BrowserCoreContainer
import com.YashDoesCode.browser.core.domain.model.Bookmark
import com.YashDoesCode.browser.core.domain.model.HistoryEntry
import com.YashDoesCode.browser.core.domain.model.Tab
import com.YashDoesCode.browser.core.domain.usecase.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FullBrowserViewModel(
    private val getBookmarksUseCase: GetBookmarksUseCase,
    private val getHistoryUseCase: GetHistoryUseCase,
    private val addBookmarkUseCase: AddBookmarkUseCase,
    private val deleteBookmarkUseCase: DeleteBookmarkUseCase,
    private val isBookmarkedUseCase: IsBookmarkedUseCase,
    private val addHistoryUseCase: AddHistoryUseCase,
    private val clearHistoryUseCase: ClearHistoryUseCase
) : ViewModel() {

    constructor(container: BrowserCoreContainer) : this(
        getBookmarksUseCase = container.getBookmarksUseCase,
        getHistoryUseCase = container.getHistoryUseCase,
        addBookmarkUseCase = container.addBookmarkUseCase,
        deleteBookmarkUseCase = container.deleteBookmarkUseCase,
        isBookmarkedUseCase = container.isBookmarkedUseCase,
        addHistoryUseCase = container.addHistoryUseCase,
        clearHistoryUseCase = container.clearHistoryUseCase
    )

    private val _uiState = MutableStateFlow(FullBrowserUiState())
    val uiState: StateFlow<FullBrowserUiState> = _uiState.asStateFlow()

    val bookmarks: StateFlow<List<Bookmark>> = getBookmarksUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val history: StateFlow<List<HistoryEntry>> = getHistoryUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onAction(action: FullBrowserAction) {
        when (action) {
            is FullBrowserAction.NavigateToUrl -> navigateToUrl(action.input)
            is FullBrowserAction.NewTab -> newTab(action.url)
            is FullBrowserAction.SwitchTab -> switchTab(action.tabId)
            is FullBrowserAction.CloseTab -> closeTab(action.tabId)
            is FullBrowserAction.GoBack -> {} // Handled by webview state / UI
            is FullBrowserAction.GoForward -> {}
            is FullBrowserAction.Reload -> {}
            is FullBrowserAction.Stop -> {}
            is FullBrowserAction.ToggleDesktopMode -> toggleDesktopMode()
            is FullBrowserAction.ToggleBookmark -> toggleBookmark()
            is FullBrowserAction.ShowBookmarks -> _uiState.update { it.copy(showBookmarksDialog = action.show) }
            is FullBrowserAction.ShowHistory -> _uiState.update { it.copy(showHistoryDialog = action.show) }
            is FullBrowserAction.ShowTabsOverview -> _uiState.update { it.copy(showTabsOverview = action.show) }
            is FullBrowserAction.UpdateTabProgress -> updateTabProgress(action)
        }
    }

    private fun navigateToUrl(input: String) {
        val formattedUrl = parseInputToUrl(input)
        val activeId = _uiState.value.activeTabId
        _uiState.update { state ->
            val updatedTabs = state.tabs.map { tab ->
                if (tab.id == activeId) {
                    tab.copy(url = formattedUrl, isLoading = true, progress = 0)
                } else tab
            }
            state.copy(tabs = updatedTabs)
        }
        checkIsBookmarked(formattedUrl)
    }

    private fun newTab(url: String) {
        val newId = "tab_${System.currentTimeMillis()}"
        val formattedUrl = parseInputToUrl(url)
        val newTab = Tab(id = newId, url = formattedUrl)
        _uiState.update { state ->
            state.copy(
                tabs = state.tabs + newTab,
                activeTabId = newId,
                showTabsOverview = false
            )
        }
        checkIsBookmarked(formattedUrl)
    }

    private fun switchTab(tabId: String) {
        _uiState.update { it.copy(activeTabId = tabId, showTabsOverview = false) }
        val activeTab = _uiState.value.activeTab
        checkIsBookmarked(activeTab.url)
    }

    private fun closeTab(tabId: String) {
        val state = _uiState.value
        if (state.tabs.size <= 1) return

        val updatedTabs = state.tabs.filterNot { it.id == tabId }
        val newActiveId = if (state.activeTabId == tabId) {
            updatedTabs.last().id
        } else {
            state.activeTabId
        }

        _uiState.update { it.copy(tabs = updatedTabs, activeTabId = newActiveId) }
        val activeTab = _uiState.value.activeTab
        checkIsBookmarked(activeTab.url)
    }

    private fun toggleDesktopMode() {
        val activeId = _uiState.value.activeTabId
        _uiState.update { state ->
            val updatedTabs = state.tabs.map { tab ->
                if (tab.id == activeId) {
                    tab.copy(isDesktopMode = !tab.isDesktopMode)
                } else tab
            }
            state.copy(tabs = updatedTabs)
        }
    }

    private fun toggleBookmark() {
        val activeTab = _uiState.value.activeTab
        val isCurrentlyBookmarked = _uiState.value.isBookmarked
        viewModelScope.launch {
            if (isCurrentlyBookmarked) {
                deleteBookmarkUseCase(activeTab.url)
                _uiState.update { it.copy(isBookmarked = false) }
            } else {
                addBookmarkUseCase(
                    title = activeTab.title ?: activeTab.url,
                    url = activeTab.url
                )
                _uiState.update { it.copy(isBookmarked = true) }
            }
        }
    }

    private fun checkIsBookmarked(url: String) {
        viewModelScope.launch {
            val bookmarked = isBookmarkedUseCase(url)
            _uiState.update { it.copy(isBookmarked = bookmarked) }
        }
    }

    private fun updateTabProgress(action: FullBrowserAction.UpdateTabProgress) {
        _uiState.update { state ->
            val updatedTabs = state.tabs.map { tab ->
                if (tab.id == action.tabId) {
                    val newTitle = action.title ?: tab.title
                    val newUrl = action.url ?: tab.url
                    tab.copy(
                        progress = action.progress,
                        isLoading = action.progress < 100,
                        title = newTitle,
                        url = newUrl,
                        canGoBack = action.canGoBack,
                        canGoForward = action.canGoForward
                    )
                } else tab
            }
            state.copy(tabs = updatedTabs)
        }

        // Save history entry when page completes loading
        if (action.progress == 100 && action.url != null) {
            viewModelScope.launch {
                addHistoryUseCase(
                    url = action.url,
                    title = action.title ?: action.url
                )
            }
            checkIsBookmarked(action.url)
        }
    }

    fun deleteBookmark(url: String) {
        viewModelScope.launch {
            deleteBookmarkUseCase(url)
            checkIsBookmarked(_uiState.value.activeTab.url)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            clearHistoryUseCase()
        }
    }

    fun parseInputToUrl(input: String): String {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return "https://www.google.com"
        return when {
            trimmed.startsWith("http://", ignoreCase = true) ||
            trimmed.startsWith("https://", ignoreCase = true) -> trimmed
            trimmed.contains(".") && !trimmed.contains(" ") -> "https://$trimmed"
            else -> "https://www.google.com/search?q=${java.net.URLEncoder.encode(trimmed, "UTF-8")}"
        }
    }

    class Factory(private val container: BrowserCoreContainer) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FullBrowserViewModel(container) as T
        }
    }
}
