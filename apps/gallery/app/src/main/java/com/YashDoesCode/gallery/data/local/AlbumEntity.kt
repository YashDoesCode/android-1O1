package com.YashDoesCode.gallery.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.YashDoesCode.gallery.domain.model.Album

@Entity(tableName = "albums")
data class AlbumEntity(
    @PrimaryKey val id: String,
    val name: String,
    val coverUri: String?,
    val imageCount: Int,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    fun toDomain(): Album = Album(
        id = id,
        name = name,
        coverUri = coverUri,
        imageCount = imageCount
    )

    companion object {
        fun fromDomain(album: Album): AlbumEntity = AlbumEntity(
            id = album.id,
            name = album.name,
            coverUri = album.coverUri,
            imageCount = album.imageCount
        )
    }
}
