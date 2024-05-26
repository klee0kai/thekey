package com.github.klee0kai.thekey.core.di.modules

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.stone.annotations.module.Provide
import com.github.klee0kai.thekey.core.di.DefaultDispatcher
import com.github.klee0kai.thekey.core.di.IODispatcher
import com.github.klee0kai.thekey.core.di.JniDispatcher
import com.github.klee0kai.thekey.core.di.MainDispatcher
import com.github.klee0kai.thekey.core.utils.common.SafeContextScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@Module
interface CoroutineModule {

    @DefaultDispatcher
    @Provide(cache = Provide.CacheType.Strong)
    fun defaultExecutor(): Executor {
        return Executors.newFixedThreadPool(5)
    }

    @JniDispatcher
    @Provide(cache = Provide.CacheType.Strong)
    fun jniDispatcher(): CoroutineDispatcher {
        return Executors.newFixedThreadPool(5).asCoroutineDispatcher()
    }

    @MainDispatcher
    fun mainDispatcher(): CoroutineDispatcher = Dispatchers.Main.immediate

    @IODispatcher
    fun ioDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @DefaultDispatcher
    fun defaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @MainDispatcher
    fun mainThreadScope(
        @MainDispatcher
        dispatcher: CoroutineDispatcher,
    ): SafeContextScope = SafeContextScope(dispatcher + SupervisorJob())

    @IODispatcher
    fun ioThreadScope(
        @IODispatcher
        dispatcher: CoroutineDispatcher,
    ): SafeContextScope = SafeContextScope(dispatcher + SupervisorJob())

    @DefaultDispatcher
    fun defaultThreadScope(
        @DefaultDispatcher
        dispatcher: CoroutineDispatcher,
    ): SafeContextScope = SafeContextScope(dispatcher + SupervisorJob())

}


