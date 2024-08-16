package com.github.klee0kai.thekey.app.di.modules

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.stone.annotations.module.Provide
import com.github.klee0kai.thekey.app.engine.editstorage.EditStorageEngine
import com.github.klee0kai.thekey.app.engine.editstorage.EditStorageSuspended
import com.github.klee0kai.thekey.app.engine.findstorage.FindStorageEngine
import com.github.klee0kai.thekey.app.engine.findstorage.FindStorageSuspended
import com.github.klee0kai.thekey.app.engine.storage.CryptStorage
import com.github.klee0kai.thekey.app.engine.storage.CryptStorageSuspended
import com.github.klee0kai.thekey.app.engine.storage.K1Storage
import com.github.klee0kai.thekey.app.engine.storage.K2Storage
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier

@Module
abstract class EngineModule {

    @Provide(cache = Provide.CacheType.Strong)
    abstract fun findStoragesEngine(): FindStorageEngine

    @Provide(cache = Provide.CacheType.Strong)
    abstract fun findStoragesSuspend(): FindStorageSuspended

    @Provide(cache = Provide.CacheType.Soft)
    open fun cryptStorageEngine(id: StorageIdentifier): CryptStorage? {
        return when (id.version) {
            1 -> K1Storage(
                engineIdentifier = id.engineIdentifier,
                storagePath = id.path,
                fileDescriptor = id.fileDescriptor,
            )

            2 -> K2Storage(
                engineIdentifier = id.engineIdentifier,
                storagePath = id.path,
                fileDescriptor = id.fileDescriptor,
            )

            else -> null
        }
    }

    @Provide(cache = Provide.CacheType.Factory)
    open fun cryptStorageEngineSuspended(id: StorageIdentifier): CryptStorageSuspended? {
        return when (id.version) {
            1, 2 -> CryptStorageSuspended(id)
            else -> null
        }
    }

    @Provide(cache = Provide.CacheType.Soft)
    abstract fun editStorageEngine(): EditStorageEngine

    @Provide(cache = Provide.CacheType.Soft)
    abstract fun editStorageEngineSuspended(): EditStorageSuspended

}