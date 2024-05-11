package com.github.klee0kai.thekey.app.utils.log

import timber.log.Timber

object TimberConfig {

    fun init() {
        Timber.plant(Timber.DebugTree())
    }

}