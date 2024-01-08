package com.github.klee0kai.thekey.app

import android.app.Application
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.utils.log.TimberConfig

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        DI.app(this)
        TimberConfig.init()
    }

}