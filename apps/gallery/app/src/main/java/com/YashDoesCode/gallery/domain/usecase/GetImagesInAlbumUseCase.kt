package com.YashDoesCode.gallery.domain.usecase

import com.YashDoesCode.gallery.domain.model.MediaImage
import com.YashDoesCode.gallery.domain.repository.GalleryRepository
import kotlinx.coroutines.flow.Flow

class GetImagesInAlbumUseCase(
    private val repository: GalleryRepository
) {
    operator fun invoke(bucketId: String? = null): Flow<List<MediaImage>> =
        repository.getImagesInAlbum(bucketId)
}
