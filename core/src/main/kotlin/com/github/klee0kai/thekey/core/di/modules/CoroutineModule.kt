package com.github.klee0kai.thekey.core.di.modules

import androidx.compose.ui.MotionDurationScale
import androidx.compose.ui.platform.AndroidUiDispatcher
import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.stone.annotations.module.Provide
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
        return Executors.newFixedThreadPool(20).asCoroutineDispatcher()
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

    @AndrUiDispatcher
    fun androidUiScope(): SafeContextScope = SafeContextScope(AndroidUiDispatcher.Main)

    @AndrFastUiDispatcher
    fun androidFastUiScope(): SafeContextScope = SafeContextScope(
        AndroidUiDispatcher.Main + object : MotionDurationScale {
            override val scaleFactor: Float = 0.1f
        }
    )

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

    @Provide(cache = Provide.CacheType.Soft)
    fun fileMutex(identifier: FileIdentifier): ReadWriteMutex = ReadWriteMutex()

}


