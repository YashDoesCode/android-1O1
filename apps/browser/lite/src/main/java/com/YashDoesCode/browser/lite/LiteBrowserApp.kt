package com.YashDoesCode.browser.lite

import android.app.Application
import com.YashDoesCode.browser.core.di.BrowserCoreContainer

class LiteBrowserApp : Application() {
    lateinit var container: BrowserCoreContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = BrowserCoreContainer(this)
    }
}
