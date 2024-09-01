package com.github.klee0kai.thekey.app.engine.findstorage

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.engine.model.Storage
import com.github.klee0kai.thekey.core.di.identifiers.FileIdentifier
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

open class FindStorageSuspended {

    private val _engine = DI.findStorageEngineLazy()
    private val dispatcher = DI.jniDispatcher()

    // TODO fix fast run without mutex
    private val readVersionMutex = Mutex()

    open suspend fun findStorages(
        folder: String,
        listener: FindStorageListener,
    ) = engineRun { findStorages(folder, listener) }

    open suspend fun storageVersion(
        path: String,
    ): Int = readVersionMutex.withLock {
        engineRead(path) {
            storageVersion(path)
        }
    }

    open suspend fun storageInfo(
        path: String,
    ): Storage? = engineRead(path) {
        storageInfo(path)
    }

    open suspend fun storageInfoFromDescriptor(
        fd: Int,
    ): Storage? = engineRun {
        storageInfoFromDescriptor(fd)
    }

    private suspend fun <T> engineRun(
        block: suspend FindStorageEngine.() -> T
    ): T = withContext(dispatcher) {
        _engine().block()
    }

    private suspend fun <T> engineWrite(
        path: String,
        block: suspend FindStorageEngine.() -> T
    ): T = withContext(dispatcher) {
        val fileMutex = DI.fileMutex(FileIdentifier(path))
        fileMutex.withWriteLock {
            _engine().block()
        }
    }

    private suspend fun <T> engineRead(
        path: String,
        block: suspend FindStorageEngine.() -> T
    ): T = withContext(dispatcher) {
        val fileMutex = DI.fileMutex(FileIdentifier(path))
        fileMutex.withWriteLock {
            _engine().block()
        }
    }


}

fun FindStorageSuspended.findStoragesFlow(folder: String): Flow<Storage> = callbackFlow {
    launch(DI.ioDispatcher()) {
        findStorages(folder, object : FindStorageListener() {
            override fun onStorageFound(storage: Storage) {
                launch { send(storage) }
            }
        })

        delay(10)
        channel.close()
    }

    awaitClose()
}