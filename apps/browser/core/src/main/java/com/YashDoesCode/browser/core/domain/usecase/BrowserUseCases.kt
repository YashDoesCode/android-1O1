package com.YashDoesCode.browser.core.domain.usecase

import com.YashDoesCode.browser.core.domain.model.Bookmark
import com.YashDoesCode.browser.core.domain.model.HistoryEntry
import com.YashDoesCode.browser.core.domain.repository.BookmarkRepository
import com.YashDoesCode.browser.core.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow

class AddBookmarkUseCase(private val repository: BookmarkRepository) {
    suspend operator fun invoke(title: String, url: String) = repository.addBookmark(title, url)
}

class GetBookmarksUseCase(private val repository: BookmarkRepository) {
    operator fun invoke(): Flow<List<Bookmark>> = repository.getBookmarks()
}

class DeleteBookmarkUseCase(private val repository: BookmarkRepository) {
    suspend operator fun invoke(id: Long) = repository.deleteBookmark(id)
}

class IsBookmarkedUseCase(private val repository: BookmarkRepository) {
    suspend operator fun invoke(url: String): Boolean = repository.isBookmarked(url)
}

class AddHistoryUseCase(private val repository: HistoryRepository) {
    suspend operator fun invoke(title: String, url: String) = repository.addHistory(title, url)
}

class GetHistoryUseCase(private val repository: HistoryRepository) {
    operator fun invoke(): Flow<List<HistoryEntry>> = repository.getHistory()
}

class ClearHistoryUseCase(private val repository: HistoryRepository) {
    suspend operator fun invoke() = repository.clearHistory()
}

class DeleteHistoryUseCase(private val repository: HistoryRepository) {
    suspend operator fun invoke(id: Long) = repository.deleteHistoryEntry(id)
}
