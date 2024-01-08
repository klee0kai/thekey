package com.github.klee0kai.thekey.app

import android.view.View
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.DummyEngineModule
import com.github.klee0kai.thekey.app.di.DummyHelpersModule

object TargetDI {

    fun initDI() {
        if (View(DI.app()).isInEditMode) {
            initDummyModules()
        }
    }

    fun initDummyModules() {
        DI.initEngineModule(DummyEngineModule::class.java)
        DI.initHelpersModule(DummyHelpersModule::class.java)
    }

}