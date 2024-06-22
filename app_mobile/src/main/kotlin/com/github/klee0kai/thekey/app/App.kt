package com.github.klee0kai.thekey.app

import android.app.Application
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.utils.log.TimberConfig
import com.github.klee0kai.thekey.core.BuildConfig
import com.github.klee0kai.thekey.core.domain.model.AppConfig
import com.github.klee0kai.thekey.core.utils.log.TimberWtfTree
import timber.log.Timber
import java.lang.ref.WeakReference

class App : Application() {

    init {
        appRef = WeakReference(this)
        if (BuildConfig.DEBUG) Timber.plant(TimberWtfTree())
    }

    private val interactor by lazy { DI.startupInteractor() }

    override fun onCreate() {
        super.onCreate()
        TimberConfig.init()
        DI.ctx(this)
        DI.config(AppConfig())
        interactor.appStarted()
    }


    companion object {
        var appRef: WeakReference<App>? = null
    }

}