package com.YashDoesCode.gallery.domain.usecase

import com.YashDoesCode.gallery.domain.model.Album
import com.YashDoesCode.gallery.domain.repository.GalleryRepository
import kotlinx.coroutines.flow.Flow

class GetAlbumsUseCase(
    private val repository: GalleryRepository
) {
    operator fun invoke(): Flow<List<Album>> = repository.getAlbums()
}
