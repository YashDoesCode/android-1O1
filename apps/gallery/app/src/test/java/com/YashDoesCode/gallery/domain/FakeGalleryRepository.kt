package com.YashDoesCode.gallery.domain

import com.YashDoesCode.gallery.domain.model.Album
import com.YashDoesCode.gallery.domain.model.MediaImage
import com.YashDoesCode.gallery.domain.repository.GalleryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeGalleryRepository : GalleryRepository {

    private val _images = MutableStateFlow<List<MediaImage>>(emptyList())

    fun setImages(images: List<MediaImage>) {
        _images.value = images
    }

    override fun getAlbums(): Flow<List<Album>> {
        return _images.map { list ->
            list.groupBy { it.bucketId }.map { (bucketId, images) ->
                Album(
                    id = bucketId,
                    name = images.first().bucketName,
                    coverUri = images.first().uri,
                    imageCount = images.size
                )
            }.sortedBy { it.name }
        }
    }

    override fun getImagesInAlbum(bucketId: String?): Flow<List<MediaImage>> {
        return _images.map { list ->
            if (bucketId == null) list else list.filter { it.bucketId == bucketId }
        }
    }

    override suspend fun getImageDetails(imageId: Long): MediaImage? {
        return _images.value.firstOrNull { it.id == imageId }
    }

    override suspend fun refreshMedia() {
        // No-op for fake
    }
}
