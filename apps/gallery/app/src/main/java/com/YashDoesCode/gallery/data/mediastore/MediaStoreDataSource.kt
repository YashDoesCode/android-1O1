package com.YashDoesCode.gallery.data.mediastore

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.YashDoesCode.gallery.domain.model.Album
import com.YashDoesCode.gallery.domain.model.MediaImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaStoreDataSource(
    private val context: Context
) {

    suspend fun fetchImages(bucketId: String? = null): List<MediaImage> = withContext(Dispatchers.IO) {
        val images = mutableListOf<MediaImage>()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )

        val selection = if (bucketId != null) {
            "${MediaStore.Images.Media.BUCKET_ID} = ?"
        } else {
            null
        }
        val selectionArgs = if (bucketId != null) arrayOf(bucketId) else null
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val contentUri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        context.contentResolver.query(
            contentUri,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
            val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)
            val bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
            val bucketNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val displayName = cursor.getString(nameColumn) ?: "IMG_$id"
                val dateAdded = cursor.getLong(dateColumn)
                val sizeBytes = cursor.getLong(sizeColumn)
                val width = cursor.getInt(widthColumn)
                val height = cursor.getInt(heightColumn)
                val bId = cursor.getString(bucketIdColumn) ?: "unknown"
                val bName = cursor.getString(bucketNameColumn) ?: "Default"

                val uri = ContentUris.withAppendedId(contentUri, id).toString()

                images.add(
                    MediaImage(
                        id = id,
                        uri = uri,
                        displayName = displayName,
                        dateAdded = dateAdded,
                        sizeBytes = sizeBytes,
                        width = width,
                        height = height,
                        bucketId = bId,
                        bucketName = bName
                    )
                )
            }
        }

        images
    }

    suspend fun fetchAlbums(): List<Album> = withContext(Dispatchers.IO) {
        val allImages = fetchImages(null)
        val albumsMap = mutableMapOf<String, MutableList<MediaImage>>()

        for (image in allImages) {
            albumsMap.getOrPut(image.bucketId) { mutableListOf() }.add(image)
        }

        albumsMap.map { (bucketId, images) ->
            Album(
                id = bucketId,
                name = images.firstOrNull()?.bucketName ?: "Album",
                coverUri = images.firstOrNull()?.uri,
                imageCount = images.size
            )
        }.sortedBy { it.name.lowercase() }
    }
}
