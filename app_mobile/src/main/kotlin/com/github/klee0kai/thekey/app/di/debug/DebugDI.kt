package com.github.klee0kai.thekey.app.di.debug

import android.view.View
import com.github.klee0kai.thekey.app.di.AppComponent
import com.github.klee0kai.thekey.core.di.StartContextHolder

object DebugDI {

    fun AppComponent.initDI() {
        val context = StartContextHolder.appRef?.get()
        if (context == null || View(context).isInEditMode) {
            initDummyModules()
        }
    }

    fun AppComponent.initDummyModules() {
        initEngineModule(DummyEngineModule::class.java)
    }

}