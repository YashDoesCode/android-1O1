package com.YashDoesCode.browser.core.domain.model

data class Tab(
    val id: String,
    val title: String = "New Tab",
    val url: String = "https://www.google.com",
    val faviconUrl: String? = null,
    val progress: Int = 0,
    val isLoading: Boolean = false,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val isDesktopMode: Boolean = false
)

data class Bookmark(
    val id: Long = 0,
    val title: String,
    val url: String,
    val createdAt: Long = System.currentTimeMillis()
)

data class HistoryEntry(
    val id: Long = 0,
    val title: String,
    val url: String,
    val visitedAt: Long = System.currentTimeMillis()
)
