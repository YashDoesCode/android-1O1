package com.YashDoesCode.gallery.domain

import com.YashDoesCode.gallery.domain.model.MediaImage
import com.YashDoesCode.gallery.domain.usecase.GetImagesInAlbumUseCase
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetImagesInAlbumUseCaseTest {

    private lateinit var fakeRepository: FakeGalleryRepository
    private lateinit var getImagesInAlbumUseCase: GetImagesInAlbumUseCase

    @Before
    fun setUp() {
        fakeRepository = FakeGalleryRepository()
        getImagesInAlbumUseCase = GetImagesInAlbumUseCase(fakeRepository)
    }

    @Test
    fun `getImagesInAlbum with null bucketId returns all images`() = runTest {
        val testImages = listOf(
            MediaImage(1, "content://media/1", "img1.jpg", 1000L, 2048L, 1920, 1080, "camera", "Camera"),
            MediaImage(2, "content://media/2", "img2.jpg", 2000L, 4096L, 1920, 1080, "screenshots", "Screenshots")
        )
        fakeRepository.setImages(testImages)

        val images = getImagesInAlbumUseCase(null).first()
        assertThat(images).hasSize(2)
    }

    @Test
    fun `getImagesInAlbum with specific bucketId filters correctly`() = runTest {
        val testImages = listOf(
            MediaImage(1, "content://media/1", "img1.jpg", 1000L, 2048L, 1920, 1080, "camera", "Camera"),
            MediaImage(2, "content://media/2", "img2.jpg", 2000L, 4096L, 1920, 1080, "screenshots", "Screenshots")
        )
        fakeRepository.setImages(testImages)

        val cameraImages = getImagesInAlbumUseCase("camera").first()
        assertThat(cameraImages).hasSize(1)
        assertThat(cameraImages.first().displayName).isEqualTo("img1.jpg")
    }
}
