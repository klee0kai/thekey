package com.github.klee0kai.thekey.app.utils.log

import com.github.klee0kai.feature.firebase.FirebaseErrorTree
import com.github.klee0kai.thekey.app.App
import timber.log.Timber

object TimberConfig {

    fun init() {
        Timber.plant(Timber.DebugTree())

        App.appRef?.get()?.let { app ->
            Timber.plant(FirebaseErrorTree(app))
        }
    }

}