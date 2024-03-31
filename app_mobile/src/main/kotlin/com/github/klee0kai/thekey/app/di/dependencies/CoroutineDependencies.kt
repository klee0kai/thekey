package com.github.klee0kai.thekey.app.di.dependencies

import com.github.klee0kai.thekey.app.di.DefaultDispatcher
import com.github.klee0kai.thekey.app.di.IODispatcher
import com.github.klee0kai.thekey.app.di.JniDispatcher
import com.github.klee0kai.thekey.app.di.MainDispatcher
import com.github.klee0kai.thekey.app.utils.common.SafeContextScope
import kotlinx.coroutines.CoroutineDispatcher

interface CoroutineDependencies {

    @JniDispatcher
    fun jniDispatcher(): CoroutineDispatcher

    @MainDispatcher
    fun mainDispatcher(): CoroutineDispatcher

    @IODispatcher
    fun ioDispatcher(): CoroutineDispatcher

    @DefaultDispatcher
    fun defaultDispatcher(): CoroutineDispatcher

    @MainDispatcher
    fun mainThreadScope(): SafeContextScope

    @IODispatcher
    fun ioThreadScope(): SafeContextScope

    @DefaultDispatcher
    fun defaultThreadScope(): SafeContextScope

}