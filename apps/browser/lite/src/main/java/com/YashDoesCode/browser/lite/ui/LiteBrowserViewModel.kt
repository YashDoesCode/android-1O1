package com.YashDoesCode.browser.lite.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.YashDoesCode.browser.core.di.BrowserCoreContainer
import com.YashDoesCode.browser.core.domain.model.Bookmark
import com.YashDoesCode.browser.core.domain.model.HistoryEntry
import com.YashDoesCode.browser.core.domain.usecase.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LiteBrowserViewModel(
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

    private val _uiState = MutableStateFlow(LiteBrowserUiState())
    val uiState: StateFlow<LiteBrowserUiState> = _uiState.asStateFlow()

    val bookmarks: StateFlow<List<Bookmark>> = getBookmarksUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val history: StateFlow<List<HistoryEntry>> = getHistoryUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onAction(action: LiteBrowserAction) {
        when (action) {
            is LiteBrowserAction.NavigateToUrl -> navigateToUrl(action.input)
            is LiteBrowserAction.GoBack -> {}
            is LiteBrowserAction.GoForward -> {}
            is LiteBrowserAction.Reload -> {}
            is LiteBrowserAction.Stop -> {}
            is LiteBrowserAction.ToggleDataSaver -> _uiState.update { it.copy(isDataSaverEnabled = !it.isDataSaverEnabled) }
            is LiteBrowserAction.ToggleBookmark -> toggleBookmark()
            is LiteBrowserAction.ShowBookmarks -> _uiState.update { it.copy(showBookmarksDialog = action.show) }
            is LiteBrowserAction.ShowHistory -> _uiState.update { it.copy(showHistoryDialog = action.show) }
            is LiteBrowserAction.UpdateProgress -> updateProgress(action)
        }
    }

    private fun navigateToUrl(input: String) {
        val formattedUrl = parseInputToUrl(input)
        _uiState.update { it.copy(url = formattedUrl, isLoading = true, progress = 0) }
        checkIsBookmarked(formattedUrl)
    }

    private fun toggleBookmark() {
        val currentUrl = _uiState.value.url
        val isCurrentlyBookmarked = _uiState.value.isBookmarked
        val currentTitle = _uiState.value.title ?: currentUrl

        viewModelScope.launch {
            if (isCurrentlyBookmarked) {
                deleteBookmarkUseCase(currentUrl)
                _uiState.update { it.copy(isBookmarked = false) }
            } else {
                addBookmarkUseCase(title = currentTitle, url = currentUrl)
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

    private fun updateProgress(action: LiteBrowserAction.UpdateProgress) {
        val newTitle = action.title ?: _uiState.value.title
        val newUrl = action.url ?: _uiState.value.url

        _uiState.update { state ->
            state.copy(
                progress = action.progress,
                isLoading = action.progress < 100,
                title = newTitle,
                url = newUrl,
                canGoBack = action.canGoBack,
                canGoForward = action.canGoForward
            )
        }

        if (action.progress == 100 && action.url != null) {
            viewModelScope.launch {
                addHistoryUseCase(url = action.url, title = newTitle ?: action.url)
            }
            checkIsBookmarked(action.url)
        }
    }

    fun deleteBookmark(url: String) {
        viewModelScope.launch {
            deleteBookmarkUseCase(url)
            checkIsBookmarked(_uiState.value.url)
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
            return LiteBrowserViewModel(container) as T
        }
    }
}
