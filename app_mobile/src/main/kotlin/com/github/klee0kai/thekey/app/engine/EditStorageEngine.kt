package com.github.klee0kai.thekey.app.engine

import com.github.klee0kai.brooklyn.JniMirror
import com.github.klee0kai.thekey.app.model.Storage


@JniMirror
class EditStorageEngine {

    init {
        NativeLibLoader.loadIfNeed()
    }

    external fun findStorageInfo(path: String): Storage?

    external fun createStorage(storage: Storage): Boolean

    external fun editStorage(storage: Storage): Boolean


}