package com.github.klee0kai.thekey.app.di

import com.github.klee0kai.stone.annotations.module.Module
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
open class CoroutineModule {

    @MainDispatcher
    open fun mainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @IODispatcher
    open fun ioDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @DefaultDispatcher
    open fun defaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

}


