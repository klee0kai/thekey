package com.github.klee0kai.thekey.core.di

import com.github.klee0kai.thekey.core.domain.model.AppConfig
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private val configMutex by lazy { Mutex() }

fun CoreComponent.updateConfig(block: AppConfig.() -> AppConfig) = runBlocking {
    configMutex.withLock {
        config()
            .block()
            .let {
                config(it)
            }
    }
}