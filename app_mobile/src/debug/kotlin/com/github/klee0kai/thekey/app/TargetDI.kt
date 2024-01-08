package com.github.klee0kai.thekey.app

import android.view.View
import com.github.klee0kai.thekey.app.di.AppComponent
import com.github.klee0kai.thekey.app.di.DummyEngineModule
import com.github.klee0kai.thekey.app.di.DummyHelpersModule

object TargetDI {

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