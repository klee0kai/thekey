package com.github.klee0kai.thekey.app.engine.findstorage

import com.github.klee0kai.brooklyn.JniMirror
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.engine.NativeLibLoader
import com.github.klee0kai.thekey.app.model.Storage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

@JniMirror
open class FindStorageEngine {

    init {
        NativeLibLoader.loadIfNeed()
    }

    open external fun findStorages(folder: String, listener: FindStorageListener)

}

fun FindStorageEngine.findStoragesFlow(folder: String): Flow<Storage> = callbackFlow {
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