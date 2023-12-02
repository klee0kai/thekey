package com.github.klee0kai.thekey.app.engine

import com.github.klee0kai.brooklyn.JniMirror
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.model.Storage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

@JniMirror
class FindStorageEngine {

    external fun findStorages(folder: String, listener: FindStorageListener)

}

@JniMirror
abstract class FindStorageListener {
    open fun onStorageFound(storage: Storage) {}
}

fun FindStorageEngine.findStorages(folder: String): Flow<Storage> = callbackFlow {
    launch(DI.ioDispatcher()) {
        findStorages(folder, object : FindStorageListener() {
            override fun onStorageFound(storage: Storage) {
                launch { send(storage) }
            }
        })
    }
    awaitClose()
}