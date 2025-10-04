package com.YashDoesCode.gallery.ui

import com.YashDoesCode.gallery.domain.model.Album
import com.YashDoesCode.gallery.domain.model.GalleryPermissionState
import com.YashDoesCode.gallery.domain.model.MediaImage

sealed interface GalleryUiState {
    data object Loading : GalleryUiState
    data class PermissionRequired(val state: GalleryPermissionState) : GalleryUiState
    data class Empty(val message: String) : GalleryUiState
    data class AlbumsContent(val albums: List<Album>) : GalleryUiState
    data class PhotosContent(
        val albumName: String,
        val bucketId: String?,
        val images: List<MediaImage>
    ) : GalleryUiState
    data class Error(val message: String) : GalleryUiState
}

sealed interface GalleryAction {
    data class SelectAlbum(val album: Album) : GalleryAction
    data class SelectImage(val image: MediaImage, val index: Int) : GalleryAction
    data object NavigateBack : GalleryAction
    data class UpdatePermission(val state: GalleryPermissionState) : GalleryAction
    data object Refresh : GalleryAction
    data class OpenDetails(val image: MediaImage) : GalleryAction
    data object CloseDetails : GalleryAction
}
