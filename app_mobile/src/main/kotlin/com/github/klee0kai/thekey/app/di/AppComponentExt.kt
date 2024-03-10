package com.github.klee0kai.thekey.app.di

import com.github.klee0kai.thekey.app.model.AppConfig
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private val configMutex by lazy { Mutex() }

suspend fun AppComponent.updateConfig(block: AppConfig.() -> AppConfig) {
    configMutex.withLock {
        DI.config()
            .block()
            .let {
                DI.config(it)
            }
    }
}