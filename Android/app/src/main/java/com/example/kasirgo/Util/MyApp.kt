package com.example.kasirgo.Util

import android.app.Application
import com.chibatching.kotpref.Kotpref
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApp:Application() {
    override fun onCreate() {
        super.onCreate()
        Kotpref.init(this)
        startKoin() {
            androidContext(this@MyApp)
        }
    }
}