package com.github.klee0kai.thekey.app.di.dependencies

import com.github.klee0kai.thekey.app.di.DefaultDispatcher
import com.github.klee0kai.thekey.app.di.IODispatcher
import com.github.klee0kai.thekey.app.di.MainDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

interface CoroutineDependencies {

    @MainDispatcher
    fun mainDispatcher(): CoroutineDispatcher

    @IODispatcher
    fun ioDispatcher(): CoroutineDispatcher

    @DefaultDispatcher
    fun defaultDispatcher(): CoroutineDispatcher

    @MainDispatcher
    fun mainThreadScope(): CoroutineScope

    @IODispatcher
    fun ioThreadScope(): CoroutineScope

    @DefaultDispatcher
    fun defaultThreadScope(): CoroutineScope

}