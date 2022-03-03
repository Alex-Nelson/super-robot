package com.example.listapp

import android.app.Application
import timber.log.Timber

class ListApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }
}