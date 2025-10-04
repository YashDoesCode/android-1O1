package com.YashDoesCode.browser.core.data

import com.YashDoesCode.browser.core.data.local.BookmarkDao
import com.YashDoesCode.browser.core.data.local.BookmarkEntity
import com.YashDoesCode.browser.core.data.local.HistoryDao
import com.YashDoesCode.browser.core.data.local.HistoryEntity
import com.YashDoesCode.browser.core.data.repository.BookmarkRepositoryImpl
import com.YashDoesCode.browser.core.data.repository.HistoryRepositoryImpl
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class FakeBookmarkDao : BookmarkDao {
    private val bookmarks = MutableStateFlow<List<BookmarkEntity>>(emptyList())
    private var idCounter = 1L

    override fun getBookmarks() = bookmarks

    override suspend fun insertBookmark(entity: BookmarkEntity): Long {
        val newId = if (entity.id == 0L) idCounter++ else entity.id
        val list = bookmarks.value.toMutableList()
        list.add(0, entity.copy(id = newId))
        bookmarks.value = list
        return newId
    }

    override suspend fun deleteBookmark(id: Long) {
        bookmarks.value = bookmarks.value.filter { it.id != id }
    }

    override suspend fun countBookmarkByUrl(url: String): Int {
        return bookmarks.value.count { it.url == url }
    }
}

class FakeHistoryDao : HistoryDao {
    private val history = MutableStateFlow<List<HistoryEntity>>(emptyList())
    private var idCounter = 1L

    override fun getHistory() = history

    override suspend fun insertHistory(entity: HistoryEntity): Long {
        val newId = if (entity.id == 0L) idCounter++ else entity.id
        val list = history.value.toMutableList()
        list.add(0, entity.copy(id = newId))
        history.value = list
        return newId
    }

    override suspend fun clearHistory() {
        history.value = emptyList()
    }

    override suspend fun deleteHistoryEntry(id: Long) {
        history.value = history.value.filter { it.id != id }
    }
}

class BrowserCoreRepositoryTest {

    private lateinit var bookmarkDao: FakeBookmarkDao
    private lateinit var historyDao: FakeHistoryDao
    private lateinit var bookmarkRepository: BookmarkRepositoryImpl
    private lateinit var historyRepository: HistoryRepositoryImpl

    @Before
    fun setUp() {
        bookmarkDao = FakeBookmarkDao()
        historyDao = FakeHistoryDao()
        bookmarkRepository = BookmarkRepositoryImpl(bookmarkDao)
        historyRepository = HistoryRepositoryImpl(historyDao)
    }

    @Test
    fun `addBookmark saves entity and isBookmarked returns true`() = runTest {
        bookmarkRepository.addBookmark("Google", "https://google.com")

        val list = bookmarkRepository.getBookmarks().first()
        assertThat(list).hasSize(1)
        assertThat(list.first().title).isEqualTo("Google")
        assertThat(bookmarkRepository.isBookmarked("https://google.com")).isTrue()
    }

    @Test
    fun `deleteBookmark removes item`() = runTest {
        bookmarkRepository.addBookmark("GitHub", "https://github.com")
        val item = bookmarkRepository.getBookmarks().first().first()

        bookmarkRepository.deleteBookmark(item.id)

        val list = bookmarkRepository.getBookmarks().first()
        assertThat(list).isEmpty()
    }

    @Test
    fun `addHistory saves entry and clearHistory empties database`() = runTest {
        historyRepository.addHistory("Kotlin", "https://kotlinlang.org")
        historyRepository.addHistory("Android", "https://developer.android.com")

        var list = historyRepository.getHistory().first()
        assertThat(list).hasSize(2)

        historyRepository.clearHistory()
        list = historyRepository.getHistory().first()
        assertThat(list).isEmpty()
    }
}
