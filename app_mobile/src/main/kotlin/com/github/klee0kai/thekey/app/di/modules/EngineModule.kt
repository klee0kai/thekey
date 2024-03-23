package com.github.klee0kai.thekey.app.di.modules

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.stone.annotations.module.Provide
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.engine.StorageVersionNotSupported
import com.github.klee0kai.thekey.app.engine.findstorage.EditStorageEngine
import com.github.klee0kai.thekey.app.engine.findstorage.FindStorageEngine
import com.github.klee0kai.thekey.app.engine.storage.CryptStorage
import com.github.klee0kai.thekey.app.engine.storage.K1Storage

@Module
abstract class EngineModule {

    @Provide(cache = Provide.CacheType.Strong)
    abstract fun findStoragesEngine(): FindStorageEngine

    @Provide(cache = Provide.CacheType.Soft)
    open fun cryptStorageEngine(id: StorageIdentifier): CryptStorage {
        return when (id.version) {
            1 -> K1Storage(id.path)
            else -> throw StorageVersionNotSupported("storage version ${id.version} not supported")
        }
    }

    @Provide(cache = Provide.CacheType.Soft)
    abstract fun editStorageEngine(): EditStorageEngine


}