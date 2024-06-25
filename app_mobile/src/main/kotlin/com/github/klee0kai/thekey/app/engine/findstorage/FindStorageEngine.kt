package com.github.klee0kai.thekey.app.engine.findstorage

import com.github.klee0kai.brooklyn.JniMirror
import com.github.klee0kai.thekey.app.engine.NativeLibLoader
import com.github.klee0kai.thekey.app.engine.model.Storage

@JniMirror
class FindStorageEngine {

    init {
        NativeLibLoader.loadIfNeed()
    }

    external fun findStorages(folder: String, listener: FindStorageListener)

    external fun storageVersion(path: String): Int

    external fun storageInfo(path: String): Storage?

    external fun storageInfoFromDescriptor(fd: Int): Storage?

}
