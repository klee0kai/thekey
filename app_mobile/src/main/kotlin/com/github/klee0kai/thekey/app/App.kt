package com.github.klee0kai.thekey.app

import android.app.Application
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.model.AppConfig
import com.github.klee0kai.thekey.app.utils.log.TimberConfig
import java.lang.ref.WeakReference

class App : Application() {

    init {
        appRef = WeakReference(this)
    }

    override fun onCreate() {
        super.onCreate()
        DI.app(this)
        DI.config(AppConfig())
        TimberConfig.init()
    }


    companion object {
        var appRef: WeakReference<App>? = null
    }

}