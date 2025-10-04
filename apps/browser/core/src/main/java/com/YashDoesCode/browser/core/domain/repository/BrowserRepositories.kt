package com.YashDoesCode.browser.core.domain.repository

import com.YashDoesCode.browser.core.domain.model.Bookmark
import com.YashDoesCode.browser.core.domain.model.HistoryEntry
import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {
    fun getBookmarks(): Flow<List<Bookmark>>
    suspend fun addBookmark(title: String, url: String)
    suspend fun deleteBookmark(id: Long)
    suspend fun isBookmarked(url: String): Boolean
}

interface HistoryRepository {
    fun getHistory(): Flow<List<HistoryEntry>>
    suspend fun addHistory(title: String, url: String)
    suspend fun clearHistory()
    suspend fun deleteHistoryEntry(id: Long)
}
