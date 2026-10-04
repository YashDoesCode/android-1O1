package com.YashDoesCode.gallery.data.repository

import com.YashDoesCode.gallery.data.local.AlbumDao
import com.YashDoesCode.gallery.data.local.AlbumEntity
import com.YashDoesCode.gallery.data.mediastore.MediaStoreDataSource
import com.YashDoesCode.gallery.domain.model.Album
import com.YashDoesCode.gallery.domain.model.MediaImage
import com.YashDoesCode.gallery.domain.repository.GalleryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class GalleryRepositoryImpl(
    private val mediaStoreDataSource: MediaStoreDataSource,
    private val albumDao: AlbumDao
) : GalleryRepository {

    override fun getAlbums(): Flow<List<Album>> = flow {
        // First emit from room cache if available
        // Then query fresh MediaStore and update cache
        try {
            val freshAlbums = mediaStoreDataSource.fetchAlbums()
            if (freshAlbums.isNotEmpty()) {
                albumDao.clearAlbums()
                albumDao.insertAlbums(freshAlbums.map { AlbumEntity.fromDomain(it) })
            }
            emit(freshAlbums)
        } catch (e: Exception) {
            // Fallback to cached albums
            albumDao.getCachedAlbums().map { list -> list.map { it.toDomain() } }.collect { cached ->
                emit(cached)
            }
        }
    }

    override fun getImagesInAlbum(bucketId: String?): Flow<List<MediaImage>> = flow {
        val images = mediaStoreDataSource.fetchImages(bucketId)
        emit(images)
    }

    override suspend fun getImageDetails(imageId: Long): MediaImage? {
        val all = mediaStoreDataSource.fetchImages(null)
        return all.firstOrNull { it.id == imageId }
    }

    override suspend fun refreshMedia() {
        val freshAlbums = mediaStoreDataSource.fetchAlbums()
        if (freshAlbums.isNotEmpty()) {
            albumDao.clearAlbums()
            albumDao.insertAlbums(freshAlbums.map { AlbumEntity.fromDomain(it) })
        }
    }
}
