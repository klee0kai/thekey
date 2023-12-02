package com.github.klee0kai.thekey.app.engine

import com.github.klee0kai.brooklyn.JniIgnore
import com.github.klee0kai.brooklyn.JniMirror
import com.github.klee0kai.thekey.app.model.Storage

@JniMirror
class FindStorageEngine {

    @JniIgnore
    var listener: FindStorageListener? = null

    external fun findStorages(folder: String)

    fun onStorageFound(storage: Storage) {
        listener?.onStorageFound(storage)
    }

}


fun interface FindStorageListener {
    fun onStorageFound(storage: Storage)
}