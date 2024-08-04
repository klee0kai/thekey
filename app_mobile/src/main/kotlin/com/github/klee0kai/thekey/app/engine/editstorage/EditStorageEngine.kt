package com.github.klee0kai.thekey.app.engine.editstorage

import com.github.klee0kai.brooklyn.JniMirror
import com.github.klee0kai.thekey.app.engine.NativeLibLoader
import com.github.klee0kai.thekey.app.engine.model.Storage

@JniMirror
class EditStorageEngine {

    init {
        NativeLibLoader.loadIfNeed()
    }

    external fun findStorageInfo(path: String): Storage?

    external fun createStorage(storage: Storage): Int

    external fun editStorage(storage: Storage): Int

    external fun move(from: String, to: String): Int

    external fun changePassw(path: String, currentPassw: String, newPassw: String)

}

