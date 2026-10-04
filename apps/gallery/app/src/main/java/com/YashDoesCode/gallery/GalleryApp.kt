package com.YashDoesCode.gallery

import android.app.Application
import com.YashDoesCode.gallery.di.GalleryContainer

class GalleryApp : Application() {
    lateinit var container: GalleryContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = GalleryContainer(this)
    }
}
