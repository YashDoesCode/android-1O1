package com.YashDoesCode.gallery.di

import android.content.Context
import com.YashDoesCode.gallery.data.local.GalleryDatabase
import com.YashDoesCode.gallery.data.mediastore.MediaStoreDataSource
import com.YashDoesCode.gallery.data.repository.GalleryRepositoryImpl
import com.YashDoesCode.gallery.domain.repository.GalleryRepository
import com.YashDoesCode.gallery.domain.usecase.GetAlbumsUseCase
import com.YashDoesCode.gallery.domain.usecase.GetImageDetailsUseCase
import com.YashDoesCode.gallery.domain.usecase.GetImagesInAlbumUseCase
import com.YashDoesCode.gallery.domain.usecase.GetPagedImagesUseCase

class GalleryContainer(context: Context) {
    private val database: GalleryDatabase = GalleryDatabase.getInstance(context)
    private val mediaStoreDataSource: MediaStoreDataSource = MediaStoreDataSource(context)

    val repository: GalleryRepository = GalleryRepositoryImpl(
        context = context,
        mediaStoreDataSource = mediaStoreDataSource,
        albumDao = database.albumDao()
    )

    val getAlbumsUseCase: GetAlbumsUseCase = GetAlbumsUseCase(repository)
    val getImagesInAlbumUseCase: GetImagesInAlbumUseCase = GetImagesInAlbumUseCase(repository)
    val getPagedImagesUseCase: GetPagedImagesUseCase = GetPagedImagesUseCase(repository)
    val getImageDetailsUseCase: GetImageDetailsUseCase = GetImageDetailsUseCase(repository)
}
