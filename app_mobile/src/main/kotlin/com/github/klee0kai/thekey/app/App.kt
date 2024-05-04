package com.github.klee0kai.thekey.app

import android.app.Application
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.domain.model.AppConfig
import com.github.klee0kai.thekey.app.utils.log.TimberConfig
import java.lang.ref.WeakReference

class App : Application() {

    init {
        appRef = WeakReference(this)
    }

    override fun onCreate() {
        super.onCreate()
        DI.ctx(this)
        DI.config(AppConfig())
        TimberConfig.init()
    }


    companion object {
        var appRef: WeakReference<App>? = null
    }

}