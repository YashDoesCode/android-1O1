package com.YashDoesCode.browser

import android.app.Application
import com.YashDoesCode.browser.core.di.BrowserCoreContainer

class BrowserApp : Application() {
    lateinit var container: BrowserCoreContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = BrowserCoreContainer(this)
    }
}
