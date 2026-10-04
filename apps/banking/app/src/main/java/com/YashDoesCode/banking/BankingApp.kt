package com.YashDoesCode.banking

import android.app.Application
import com.YashDoesCode.banking.di.BankingContainer

class BankingApp : Application() {
    lateinit var container: BankingContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = BankingContainer(this)
    }
}
