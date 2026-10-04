package com.YashDoesCode.browser.ui

import com.YashDoesCode.browser.core.domain.model.Bookmark
import com.YashDoesCode.browser.core.domain.model.HistoryEntry
import com.YashDoesCode.browser.core.domain.repository.BookmarkRepository
import com.YashDoesCode.browser.core.domain.repository.HistoryRepository
import com.YashDoesCode.browser.core.domain.usecase.*
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

class FakeBookmarkRepository : BookmarkRepository {
    private val bookmarksFlow = MutableStateFlow<List<Bookmark>>(emptyList())
    private var idCounter = 1L

    override fun getBookmarks(): Flow<List<Bookmark>> = bookmarksFlow

    override suspend fun addBookmark(title: String, url: String) {
        val newBookmark = Bookmark(idCounter++, title, url, System.currentTimeMillis())
        bookmarksFlow.value = listOf(newBookmark) + bookmarksFlow.value
    }

    override suspend fun deleteBookmark(id: Long) {
        bookmarksFlow.value = bookmarksFlow.value.filterNot { it.id == id }
    }

    override suspend fun deleteBookmarkByUrl(url: String) {
        bookmarksFlow.value = bookmarksFlow.value.filterNot { it.url == url }
    }

    override suspend fun isBookmarked(url: String): Boolean {
        return bookmarksFlow.value.any { it.url == url }
    }
}

class FakeHistoryRepository : HistoryRepository {
    private val historyFlow = MutableStateFlow<List<HistoryEntry>>(emptyList())
    private var idCounter = 1L

    override fun getHistory(): Flow<List<HistoryEntry>> = historyFlow

    override suspend fun addHistory(title: String, url: String) {
        val entry = HistoryEntry(idCounter++, title, url, System.currentTimeMillis())
        historyFlow.value = listOf(entry) + historyFlow.value
    }

    override suspend fun clearHistory() {
        historyFlow.value = emptyList()
    }

    override suspend fun deleteHistoryEntry(id: Long) {
        historyFlow.value = historyFlow.value.filterNot { it.id == id }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class FullBrowserViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var bookmarkRepo: FakeBookmarkRepository
    private lateinit var historyRepo: FakeHistoryRepository
    private lateinit var viewModel: FullBrowserViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        bookmarkRepo = FakeBookmarkRepository()
        historyRepo = FakeHistoryRepository()

        viewModel = FullBrowserViewModel(
            getBookmarksUseCase = GetBookmarksUseCase(bookmarkRepo),
            getHistoryUseCase = GetHistoryUseCase(historyRepo),
            addBookmarkUseCase = AddBookmarkUseCase(bookmarkRepo),
            deleteBookmarkUseCase = DeleteBookmarkUseCase(bookmarkRepo),
            isBookmarkedUseCase = IsBookmarkedUseCase(bookmarkRepo),
            addHistoryUseCase = AddHistoryUseCase(historyRepo),
            clearHistoryUseCase = ClearHistoryUseCase(historyRepo)
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `parseInputToUrl correctly formats standard URLs and search queries`() {
        val httpUrl = viewModel.parseInputToUrl("http://example.com")
        assertThat(httpUrl).isEqualTo("http://example.com")

        val httpsUrl = viewModel.parseInputToUrl("https://kotlinlang.org")
        assertThat(httpsUrl).isEqualTo("https://kotlinlang.org")

        val domain = viewModel.parseInputToUrl("github.com")
        assertThat(domain).isEqualTo("https://github.com")

        val searchQuery = viewModel.parseInputToUrl("android jetpack compose")
        assertThat(searchQuery).contains("google.com/search?q=")
        assertThat(searchQuery).contains("android")
    }

    @Test
    fun `navigateToUrl updates active tab url`() = runTest {
        viewModel.onAction(FullBrowserAction.NavigateToUrl("https://developer.android.com"))
        testScheduler.advanceUntilIdle()

        val activeTab = viewModel.uiState.value.activeTab
        assertThat(activeTab.url).isEqualTo("https://developer.android.com")
    }

    @Test
    fun `newTab adds tab and sets activeTabId`() = runTest {
        val initialTabCount = viewModel.uiState.value.tabs.size
        viewModel.onAction(FullBrowserAction.NewTab("https://news.ycombinator.com"))
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.tabs.size).isEqualTo(initialTabCount + 1)
        assertThat(state.activeTab.url).isEqualTo("https://news.ycombinator.com")
    }

    @Test
    fun `closeTab removes tab when multiple tabs exist`() = runTest {
        viewModel.onAction(FullBrowserAction.NewTab("https://example.org"))
        testScheduler.advanceUntilIdle()
        val firstTabId = viewModel.uiState.value.tabs.first().id

        viewModel.onAction(FullBrowserAction.CloseTab(firstTabId))
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.tabs.size).isEqualTo(1)
        assertThat(state.tabs.none { it.id == firstTabId }).isTrue()
    }

    @Test
    fun `toggleDesktopMode toggles desktop mode on active tab`() = runTest {
        assertThat(viewModel.uiState.value.activeTab.isDesktopMode).isFalse()

        viewModel.onAction(FullBrowserAction.ToggleDesktopMode)
        testScheduler.advanceUntilIdle()

        assertThat(viewModel.uiState.value.activeTab.isDesktopMode).isTrue()
    }

    @Test
    fun `toggleBookmark adds and removes bookmark`() = runTest {
        viewModel.onAction(FullBrowserAction.NavigateToUrl("https://android.com"))
        testScheduler.advanceUntilIdle()

        viewModel.onAction(FullBrowserAction.ToggleBookmark)
        testScheduler.advanceUntilIdle()

        assertThat(viewModel.uiState.value.isBookmarked).isTrue()

        viewModel.onAction(FullBrowserAction.ToggleBookmark)
        testScheduler.advanceUntilIdle()

        assertThat(viewModel.uiState.value.isBookmarked).isFalse()
    }
}
