package com.github.klee0kai.thekey.app

import android.view.View
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.DummyEngineModule

object TargetDI {

    fun initDI() {
        if (View(DI.app()).isInEditMode) {
            DI.initEngineModule(DummyEngineModule::class.java)

        }
    }

}