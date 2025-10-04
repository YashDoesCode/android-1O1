package com.YashDoesCode.gallery.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.YashDoesCode.gallery.domain.model.Album
import com.YashDoesCode.gallery.domain.model.GalleryPermissionState
import com.YashDoesCode.gallery.domain.model.MediaImage
import com.YashDoesCode.gallery.domain.usecase.GetAlbumsUseCase
import com.YashDoesCode.gallery.domain.usecase.GetImageDetailsUseCase
import com.YashDoesCode.gallery.domain.usecase.GetImagesInAlbumUseCase
import com.YashDoesCode.gallery.domain.usecase.GetPagedImagesUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class GalleryViewModel(
    private val getAlbumsUseCase: GetAlbumsUseCase,
    private val getImagesInAlbumUseCase: GetImagesInAlbumUseCase,
    private val getPagedImagesUseCase: GetPagedImagesUseCase,
    private val getImageDetailsUseCase: GetImageDetailsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<GalleryUiState>(GalleryUiState.Loading)
    val uiState: StateFlow<GalleryUiState> = _uiState.asStateFlow()

    private val _selectedImage = MutableStateFlow<MediaImage?>(null)
    val selectedImage: StateFlow<MediaImage?> = _selectedImage.asStateFlow()

    private val _isDetailsOpen = MutableStateFlow(false)
    val isDetailsOpen: StateFlow<Boolean> = _isDetailsOpen.asStateFlow()

    private val _currentBucketId = MutableStateFlow<String?>(null)
    val currentBucketId: StateFlow<String?> = _currentBucketId.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val pagedPhotos: Flow<PagingData<MediaImage>> = _currentBucketId
        .flatMapLatest { bucketId ->
            getPagedImagesUseCase(bucketId)
        }
        .cachedIn(viewModelScope)

    private var currentAlbum: Album? = null

    fun onAction(action: GalleryAction) {
        when (action) {
            is GalleryAction.UpdatePermission -> handlePermissionUpdate(action.state)
            is GalleryAction.SelectAlbum -> loadAlbumPhotos(action.album)
            is GalleryAction.SelectImage -> {
                _selectedImage.value = action.image
            }
            GalleryAction.NavigateBack -> handleBackNavigation()
            GalleryAction.Refresh -> loadAlbums()
            is GalleryAction.OpenDetails -> {
                _selectedImage.value = action.image
                _isDetailsOpen.value = true
            }
            GalleryAction.CloseDetails -> {
                _isDetailsOpen.value = false
            }
        }
    }

    private fun handlePermissionUpdate(state: GalleryPermissionState) {
        when (state) {
            GalleryPermissionState.Granted, GalleryPermissionState.Partial -> {
                loadAlbums()
            }
            GalleryPermissionState.Denied, GalleryPermissionState.PermanentlyDenied -> {
                _uiState.value = GalleryUiState.PermissionRequired(state)
            }
        }
    }

    fun loadAlbums() {
        _uiState.value = GalleryUiState.Loading
        viewModelScope.launch {
            getAlbumsUseCase()
                .catch { e ->
                    _uiState.value = GalleryUiState.Error(e.message ?: "Failed to load albums")
                }
                .collect { albums ->
                    if (albums.isEmpty()) {
                        _uiState.value = GalleryUiState.Empty("No photo albums found on device")
                    } else {
                        currentAlbum = null
                        _uiState.value = GalleryUiState.AlbumsContent(albums)
                    }
                }
        }
    }

    private fun loadAlbumPhotos(album: Album) {
        currentAlbum = album
        _currentBucketId.value = album.id
        _uiState.value = GalleryUiState.Loading
        viewModelScope.launch {
            getImagesInAlbumUseCase(album.id)
                .catch { e ->
                    _uiState.value = GalleryUiState.Error(e.message ?: "Failed to load photos")
                }
                .collect { images ->
                    if (images.isEmpty()) {
                        _uiState.value = GalleryUiState.Empty("This album is empty")
                    } else {
                        _uiState.value = GalleryUiState.PhotosContent(
                            albumName = album.name,
                            bucketId = album.id,
                            images = images
                        )
                    }
                }
        }
    }

    private fun handleBackNavigation() {
        if (_selectedImage.value != null) {
            _selectedImage.value = null
            _isDetailsOpen.value = false
        } else if (currentAlbum != null) {
            loadAlbums()
        }
    }

    class Factory(
        private val getAlbumsUseCase: GetAlbumsUseCase,
        private val getImagesInAlbumUseCase: GetImagesInAlbumUseCase,
        private val getPagedImagesUseCase: GetPagedImagesUseCase,
        private val getImageDetailsUseCase: GetImageDetailsUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return GalleryViewModel(
                getAlbumsUseCase,
                getImagesInAlbumUseCase,
                getPagedImagesUseCase,
                getImageDetailsUseCase
            ) as T
        }
    }
}
