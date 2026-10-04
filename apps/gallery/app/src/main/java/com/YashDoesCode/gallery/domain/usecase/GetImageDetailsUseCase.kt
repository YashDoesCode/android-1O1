package com.YashDoesCode.gallery.domain.usecase

import com.YashDoesCode.gallery.domain.model.MediaImage
import com.YashDoesCode.gallery.domain.repository.GalleryRepository

class GetImageDetailsUseCase(
    private val repository: GalleryRepository
) {
    suspend operator fun invoke(imageId: Long): MediaImage? =
        repository.getImageDetails(imageId)
}
