package com.YashDoesCode.gallery.domain.repository

import androidx.paging.PagingData
import com.YashDoesCode.gallery.domain.model.Album
import com.YashDoesCode.gallery.domain.model.MediaImage
import kotlinx.coroutines.flow.Flow

interface GalleryRepository {
    fun getAlbums(): Flow<List<Album>>
    fun getImagesInAlbum(bucketId: String?): Flow<List<MediaImage>>
    fun getPagedImages(bucketId: String? = null): Flow<PagingData<MediaImage>>
    suspend fun getImageDetails(imageId: Long): MediaImage?
    suspend fun refreshMedia(): Unit
}
