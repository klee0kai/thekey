package com.github.klee0kai.thekey.app

import android.app.Application
import com.github.klee0kai.thekey.app.di.DI

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        DI.app(this)
        TargetDI.initDI()
    }

}