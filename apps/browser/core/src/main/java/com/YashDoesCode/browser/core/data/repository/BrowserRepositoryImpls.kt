package com.YashDoesCode.browser.core.data.repository

import com.YashDoesCode.browser.core.data.local.BookmarkDao
import com.YashDoesCode.browser.core.data.local.BookmarkEntity
import com.YashDoesCode.browser.core.data.local.HistoryDao
import com.YashDoesCode.browser.core.data.local.HistoryEntity
import com.YashDoesCode.browser.core.domain.model.Bookmark
import com.YashDoesCode.browser.core.domain.model.HistoryEntry
import com.YashDoesCode.browser.core.domain.repository.BookmarkRepository
import com.YashDoesCode.browser.core.domain.repository.HistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class BookmarkRepositoryImpl(
    private val dao: BookmarkDao
) : BookmarkRepository {

    override fun getBookmarks(): Flow<List<Bookmark>> =
        dao.getBookmarks().map { list -> list.map { it.toDomain() } }

    override suspend fun addBookmark(title: String, url: String) = withContext(Dispatchers.IO) {
        if (url.isBlank() || url == "about:blank") return@withContext
        val entity = BookmarkEntity(title = title.ifBlank { url }, url = url)
        dao.insertBookmark(entity)
        Unit
    }

    override suspend fun deleteBookmark(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteBookmark(id)
    }

    override suspend fun isBookmarked(url: String): Boolean = withContext(Dispatchers.IO) {
        if (url.isBlank()) return@withContext false
        dao.countBookmarkByUrl(url) > 0
    }
}

class HistoryRepositoryImpl(
    private val dao: HistoryDao
) : HistoryRepository {

    override fun getHistory(): Flow<List<HistoryEntry>> =
        dao.getHistory().map { list -> list.map { it.toDomain() } }

    override suspend fun addHistory(title: String, url: String) = withContext(Dispatchers.IO) {
        if (url.isBlank() || url == "about:blank") return@withContext
        val entity = HistoryEntity(title = title.ifBlank { url }, url = url)
        dao.insertHistory(entity)
        Unit
    }

    override suspend fun clearHistory() = withContext(Dispatchers.IO) {
        dao.clearHistory()
    }

    override suspend fun deleteHistoryEntry(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteHistoryEntry(id)
    }
}
