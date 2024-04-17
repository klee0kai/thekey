package com.github.klee0kai.thekey.app.di.debug

import android.view.View
import com.github.klee0kai.thekey.app.App
import com.github.klee0kai.thekey.app.di.AppComponent

object DebugDI {

    fun AppComponent.initDI() {
        val context = App.appRef?.get()
        if (context == null || View(context).isInEditMode) {
            initDummyModules()
        }
    }

    fun AppComponent.initDummyModules() {
        initEngineModule(DummyEngineModule::class.java)
        initHelpersModule(DummyHelpersModule::class.java)
    }

}