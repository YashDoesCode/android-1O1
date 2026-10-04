package com.YashDoesCode.gallery.ui

import com.YashDoesCode.gallery.domain.FakeGalleryRepository
import com.YashDoesCode.gallery.domain.model.Album
import com.YashDoesCode.gallery.domain.model.GalleryPermissionState
import com.YashDoesCode.gallery.domain.model.MediaImage
import com.YashDoesCode.gallery.domain.usecase.GetAlbumsUseCase
import com.YashDoesCode.gallery.domain.usecase.GetImageDetailsUseCase
import com.YashDoesCode.gallery.domain.usecase.GetImagesInAlbumUseCase
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GalleryViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeGalleryRepository
    private lateinit var viewModel: GalleryViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeGalleryRepository()
        viewModel = GalleryViewModel(
            getAlbumsUseCase = GetAlbumsUseCase(fakeRepository),
            getImagesInAlbumUseCase = GetImagesInAlbumUseCase(fakeRepository),
            getImageDetailsUseCase = GetImageDetailsUseCase(fakeRepository)
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `permission denied updates ui state to PermissionRequired`() {
        viewModel.onAction(GalleryAction.UpdatePermission(GalleryPermissionState.Denied))
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(GalleryUiState.PermissionRequired::class.java)
        assertThat((state as GalleryUiState.PermissionRequired).state).isEqualTo(GalleryPermissionState.Denied)
    }

    @Test
    fun `permission granted loads albums into AlbumsContent`() = runTest {
        val testImages = listOf(
            MediaImage(1, "content://media/1", "photo1.jpg", 1000L, 2048L, 1920, 1080, "camera", "Camera"),
            MediaImage(2, "content://media/2", "photo2.jpg", 2000L, 4096L, 1920, 1080, "camera", "Camera")
        )
        fakeRepository.setImages(testImages)

        viewModel.onAction(GalleryAction.UpdatePermission(GalleryPermissionState.Granted))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(GalleryUiState.AlbumsContent::class.java)
        val albums = (state as GalleryUiState.AlbumsContent).albums
        assertThat(albums).hasSize(1)
        assertThat(albums.first().name).isEqualTo("Camera")
        assertThat(albums.first().imageCount).isEqualTo(2)
    }

    @Test
    fun `selecting album transitions to PhotosContent`() = runTest {
        val testImages = listOf(
            MediaImage(1, "content://media/1", "photo1.jpg", 1000L, 2048L, 1920, 1080, "camera", "Camera")
        )
        fakeRepository.setImages(testImages)
        viewModel.onAction(GalleryAction.UpdatePermission(GalleryPermissionState.Granted))
        advanceUntilIdle()

        val album = Album("camera", "Camera", "content://media/1", 1)
        viewModel.onAction(GalleryAction.SelectAlbum(album))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(GalleryUiState.PhotosContent::class.java)
        val photos = (state as GalleryUiState.PhotosContent).images
        assertThat(photos).hasSize(1)
        assertThat(photos.first().displayName).isEqualTo("photo1.jpg")
    }

    @Test
    fun `details modal sheet opens and closes correctly`() {
        val image = MediaImage(1, "content://media/1", "photo1.jpg", 1000L, 2048L, 1920, 1080, "camera", "Camera")
        viewModel.onAction(GalleryAction.OpenDetails(image))

        assertThat(viewModel.isDetailsOpen.value).isTrue()
        assertThat(viewModel.selectedImage.value).isEqualTo(image)

        viewModel.onAction(GalleryAction.CloseDetails)
        assertThat(viewModel.isDetailsOpen.value).isFalse()
    }
}
