package com.YashDoesCode.gallery.domain.model

sealed interface GalleryPermissionState {
    data object Granted : GalleryPermissionState
    data object Partial : GalleryPermissionState
    data object Denied : GalleryPermissionState
    data object PermanentlyDenied : GalleryPermissionState
}

sealed interface GalleryResult<out T> {
    data class Success<T>(val data: T) : GalleryResult<T>
    data class Error(val message: String, val throwable: Throwable? = null) : GalleryResult<Nothing>
}
