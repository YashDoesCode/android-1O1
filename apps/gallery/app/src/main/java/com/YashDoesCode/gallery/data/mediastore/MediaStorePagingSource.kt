package com.YashDoesCode.gallery.data.mediastore

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.YashDoesCode.gallery.domain.model.MediaImage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * PagingSource for windowed MediaStore queries using ContentResolver with limit/offset arguments.
 */
class MediaStorePagingSource(
    private val context: Context,
    private val bucketId: String? = null,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : PagingSource<Int, MediaImage>() {

    override fun getRefreshKey(state: PagingState<Int, MediaImage>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(state.config.pageSize)
                ?: anchorPage?.nextKey?.minus(state.config.pageSize)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MediaImage> = withContext(ioDispatcher) {
        try {
            val offset = params.key ?: 0
            val limit = params.loadSize

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

            val queryArgs = Bundle().apply {
                putInt(ContentResolver.QUERY_ARG_LIMIT, limit)
                putInt(ContentResolver.QUERY_ARG_OFFSET, offset)
                putString(ContentResolver.QUERY_ARG_SQL_SORT_ORDER, "${MediaStore.Images.Media.DATE_ADDED} DESC")
                if (bucketId != null) {
                    putString(ContentResolver.QUERY_ARG_SQL_SELECTION, "${MediaStore.Images.Media.BUCKET_ID} = ?")
                    putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, arrayOf(bucketId))
                }
            }

            val contentUri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            context.contentResolver.query(
                contentUri,
                projection,
                queryArgs,
                null
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

            val prevKey = if (offset == 0) null else maxOf(0, offset - limit)
            val nextKey = if (images.size < limit) null else offset + images.size

            LoadResult.Page(
                data = images,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
