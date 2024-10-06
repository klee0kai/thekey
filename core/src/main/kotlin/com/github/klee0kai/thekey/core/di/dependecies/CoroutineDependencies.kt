package com.github.klee0kai.thekey.core.di.dependecies

import com.github.klee0kai.thekey.core.di.AndrFastUiDispatcher
import com.github.klee0kai.thekey.core.di.AndrUiDispatcher
import com.github.klee0kai.thekey.core.di.DefaultDispatcher
import com.github.klee0kai.thekey.core.di.IODispatcher
import com.github.klee0kai.thekey.core.di.JniDispatcher
import com.github.klee0kai.thekey.core.di.MainDispatcher
import com.github.klee0kai.thekey.core.di.identifiers.FileIdentifier
import com.github.klee0kai.thekey.core.utils.common.ReadWriteMutex
import com.github.klee0kai.thekey.core.utils.common.SafeContextScope
import kotlinx.coroutines.CoroutineDispatcher
import java.util.concurrent.Executor

interface CoroutineDependencies {

    @DefaultDispatcher
    open fun defaultExecutor(): Executor

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

    @AndrUiDispatcher
    fun androidUiScope(): SafeContextScope

    @AndrFastUiDispatcher
    fun androidFastUiScope(): SafeContextScope

    @IODispatcher
    fun ioThreadScope(): SafeContextScope

    @DefaultDispatcher
    fun defaultThreadScope(): SafeContextScope

    fun fileMutex(identifier: FileIdentifier): ReadWriteMutex

}