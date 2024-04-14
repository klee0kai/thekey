package com.github.klee0kai.thekey.app.di.modules

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.stone.annotations.module.Provide
import com.github.klee0kai.thekey.app.di.DefaultDispatcher
import com.github.klee0kai.thekey.app.di.IODispatcher
import com.github.klee0kai.thekey.app.di.JniDispatcher
import com.github.klee0kai.thekey.app.di.MainDispatcher
import com.github.klee0kai.thekey.app.utils.common.SafeContextScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@Module
open class CoroutineModule {

    @DefaultDispatcher
    @Provide(cache = Provide.CacheType.Strong)
    open fun defaultExecutor(): Executor {
        return Executors.newFixedThreadPool(5)
    }

    @JniDispatcher
    @Provide(cache = Provide.CacheType.Strong)
    open fun jniDispatcher(): CoroutineDispatcher {
        return Executors.newFixedThreadPool(5).asCoroutineDispatcher()
    }

    @MainDispatcher
    open fun mainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @IODispatcher
    open fun ioDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @DefaultDispatcher
    open fun defaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @MainDispatcher
    open fun mainThreadScope(
        @MainDispatcher
        dispatcher: CoroutineDispatcher,
    ): SafeContextScope = SafeContextScope(dispatcher + SupervisorJob())

    @IODispatcher
    open fun ioThreadScope(
        @IODispatcher
        dispatcher: CoroutineDispatcher,
    ): SafeContextScope = SafeContextScope(dispatcher + SupervisorJob())

    @DefaultDispatcher
    open fun defaultThreadScope(
        @DefaultDispatcher
        dispatcher: CoroutineDispatcher,
    ): SafeContextScope = SafeContextScope(dispatcher + SupervisorJob())

}


