package com.github.klee0kai.thekey.app

import android.app.Application
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.utils.log.TimberConfig
import com.github.klee0kai.thekey.core.di.StartContextHolder
import com.github.klee0kai.thekey.core.domain.model.AppConfig
import java.lang.ref.WeakReference

class App : Application() {

    init {
        StartContextHolder.appRef = WeakReference(this)
    }

    private val interactor by lazy { DI.startupInteractor() }

    override fun onCreate() {
        super.onCreate()
        TimberConfig.init()
        DI.ctx(this)
        DI.config(AppConfig())
        interactor.appStarted()
    }

}