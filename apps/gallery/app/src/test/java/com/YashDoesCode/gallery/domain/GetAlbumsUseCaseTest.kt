package com.YashDoesCode.gallery.domain

import com.YashDoesCode.gallery.domain.model.MediaImage
import com.YashDoesCode.gallery.domain.usecase.GetAlbumsUseCase
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetAlbumsUseCaseTest {

    private lateinit var fakeRepository: FakeGalleryRepository
    private lateinit var getAlbumsUseCase: GetAlbumsUseCase

    @Before
    fun setUp() {
        fakeRepository = FakeGalleryRepository()
        getAlbumsUseCase = GetAlbumsUseCase(fakeRepository)
    }

    @Test
    fun `getAlbums returns albums grouped by bucketId with counts`() = runTest {
        val testImages = listOf(
            MediaImage(1, "content://media/1", "img1.jpg", 1000L, 2048L, 1920, 1080, "camera", "Camera"),
            MediaImage(2, "content://media/2", "img2.jpg", 2000L, 4096L, 1920, 1080, "camera", "Camera"),
            MediaImage(3, "content://media/3", "img3.jpg", 3000L, 1024L, 800, 600, "downloads", "Downloads")
        )
        fakeRepository.setImages(testImages)

        val albums = getAlbumsUseCase().first()

        assertThat(albums).hasSize(2)
        val cameraAlbum = albums.first { it.id == "camera" }
        assertThat(cameraAlbum.imageCount).isEqualTo(2)
        assertThat(cameraAlbum.name).isEqualTo("Camera")

        val downloadsAlbum = albums.first { it.id == "downloads" }
        assertThat(downloadsAlbum.imageCount).isEqualTo(1)
        assertThat(downloadsAlbum.name).isEqualTo("Downloads")
    }

    @Test
    fun `getAlbums returns empty list when no images exist`() = runTest {
        fakeRepository.setImages(emptyList())
        val albums = getAlbumsUseCase().first()
        assertThat(albums).isEmpty()
    }
}
