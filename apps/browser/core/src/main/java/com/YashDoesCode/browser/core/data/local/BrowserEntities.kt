package com.YashDoesCode.browser.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.YashDoesCode.browser.core.domain.model.Bookmark
import com.YashDoesCode.browser.core.domain.model.HistoryEntry

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val url: String,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): Bookmark = Bookmark(id, title, url, createdAt)

    companion object {
        fun fromDomain(bookmark: Bookmark): BookmarkEntity = BookmarkEntity(
            id = bookmark.id,
            title = bookmark.title,
            url = bookmark.url,
            createdAt = bookmark.createdAt
        )
    }
}

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val url: String,
    val visitedAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): HistoryEntry = HistoryEntry(id, title, url, visitedAt)

    companion object {
        fun fromDomain(entry: HistoryEntry): HistoryEntity = HistoryEntity(
            id = entry.id,
            title = entry.title,
            url = entry.url,
            visitedAt = entry.visitedAt
        )
    }
}
