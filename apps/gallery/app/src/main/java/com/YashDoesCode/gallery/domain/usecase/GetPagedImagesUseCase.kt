package com.YashDoesCode.gallery.domain.usecase

import androidx.paging.PagingData
import com.YashDoesCode.gallery.domain.model.MediaImage
import com.YashDoesCode.gallery.domain.repository.GalleryRepository
import kotlinx.coroutines.flow.Flow

class GetPagedImagesUseCase(
    private val repository: GalleryRepository
) {
    operator fun invoke(bucketId: String? = null): Flow<PagingData<MediaImage>> =
        repository.getPagedImages(bucketId)
}
