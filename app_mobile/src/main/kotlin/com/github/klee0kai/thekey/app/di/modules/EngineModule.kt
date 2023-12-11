package com.github.klee0kai.thekey.app.di.modules

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.stone.annotations.module.Provide
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.engine.CryptStorageEngine
import com.github.klee0kai.thekey.app.engine.FindStorageEngine

@Module
abstract class EngineModule {

    @Provide(cache = Provide.CacheType.Strong)
    abstract fun findStoragesEngine(): FindStorageEngine

    @Provide(cache = Provide.CacheType.Soft)
    open fun cryptStorageEngine(id: StorageIdentifier): CryptStorageEngine =
        CryptStorageEngine(id.path)

}