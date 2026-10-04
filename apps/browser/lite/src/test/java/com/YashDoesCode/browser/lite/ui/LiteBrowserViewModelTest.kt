package com.YashDoesCode.browser.lite.ui

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

class FakeLiteBookmarkRepository : BookmarkRepository {
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

class FakeLiteHistoryRepository : HistoryRepository {
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
class LiteBrowserViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var bookmarkRepo: FakeLiteBookmarkRepository
    private lateinit var historyRepo: FakeLiteHistoryRepository
    private lateinit var viewModel: LiteBrowserViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        bookmarkRepo = FakeLiteBookmarkRepository()
        historyRepo = FakeLiteHistoryRepository()

        viewModel = LiteBrowserViewModel(
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
    fun `toggleDataSaver updates isDataSaverEnabled state`() = runTest {
        assertThat(viewModel.uiState.value.isDataSaverEnabled).isTrue()

        viewModel.onAction(LiteBrowserAction.ToggleDataSaver)
        testScheduler.advanceUntilIdle()

        assertThat(viewModel.uiState.value.isDataSaverEnabled).isFalse()
    }

    @Test
    fun `navigateToUrl updates state url`() = runTest {
        viewModel.onAction(LiteBrowserAction.NavigateToUrl("wikipedia.org"))
        testScheduler.advanceUntilIdle()

        assertThat(viewModel.uiState.value.url).isEqualTo("https://wikipedia.org")
    }

    @Test
    fun `toggleBookmark saves to core bookmark repository`() = runTest {
        viewModel.onAction(LiteBrowserAction.NavigateToUrl("https://duckduckgo.com"))
        testScheduler.advanceUntilIdle()

        viewModel.onAction(LiteBrowserAction.ToggleBookmark)
        testScheduler.advanceUntilIdle()

        assertThat(viewModel.uiState.value.isBookmarked).isTrue()
    }
}
