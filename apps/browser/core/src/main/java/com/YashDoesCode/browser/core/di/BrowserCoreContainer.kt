package com.YashDoesCode.browser.core.di

import android.content.Context
import com.YashDoesCode.browser.core.data.local.BrowserDatabase
import com.YashDoesCode.browser.core.data.repository.BookmarkRepositoryImpl
import com.YashDoesCode.browser.core.data.repository.HistoryRepositoryImpl
import com.YashDoesCode.browser.core.domain.repository.BookmarkRepository
import com.YashDoesCode.browser.core.domain.repository.HistoryRepository
import com.YashDoesCode.browser.core.domain.usecase.*

class BrowserCoreContainer(context: Context) {
    private val database: BrowserDatabase = BrowserDatabase.getInstance(context)

    val bookmarkRepository: BookmarkRepository = BookmarkRepositoryImpl(database.bookmarkDao())
    val historyRepository: HistoryRepository = HistoryRepositoryImpl(database.historyDao())

    val addBookmarkUseCase = AddBookmarkUseCase(bookmarkRepository)
    val getBookmarksUseCase = GetBookmarksUseCase(bookmarkRepository)
    val deleteBookmarkUseCase = DeleteBookmarkUseCase(bookmarkRepository)
    val isBookmarkedUseCase = IsBookmarkedUseCase(bookmarkRepository)

    val addHistoryUseCase = AddHistoryUseCase(historyRepository)
    val getHistoryUseCase = GetHistoryUseCase(historyRepository)
    val clearHistoryUseCase = ClearHistoryUseCase(historyRepository)
    val deleteHistoryUseCase = DeleteHistoryUseCase(historyRepository)
}
