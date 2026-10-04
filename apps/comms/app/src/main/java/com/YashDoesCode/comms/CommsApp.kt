package com.YashDoesCode.comms

import android.app.Application
import com.YashDoesCode.comms.di.CommsContainer

class CommsApp : Application() {
    lateinit var container: CommsContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = CommsContainer(this)
    }
}
