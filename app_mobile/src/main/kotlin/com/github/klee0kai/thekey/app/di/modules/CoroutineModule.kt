package com.github.klee0kai.thekey.app.di.modules

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.thekey.app.di.DefaultDispatcher
import com.github.klee0kai.thekey.app.di.IODispatcher
import com.github.klee0kai.thekey.app.di.MainDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Module
open class CoroutineModule {

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
    ): CoroutineScope = CoroutineScope(dispatcher + SupervisorJob())

    @IODispatcher
    open fun ioThreadScope(
        @IODispatcher
        dispatcher: CoroutineDispatcher,
    ): CoroutineScope = CoroutineScope(dispatcher + SupervisorJob())

    @DefaultDispatcher
    open fun defaultThreadScope(
        @DefaultDispatcher
        dispatcher: CoroutineDispatcher,
    ): CoroutineScope = CoroutineScope(dispatcher + SupervisorJob())

}


