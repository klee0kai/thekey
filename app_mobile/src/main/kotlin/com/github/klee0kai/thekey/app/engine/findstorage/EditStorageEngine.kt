package com.github.klee0kai.thekey.app.engine.findstorage

import com.github.klee0kai.brooklyn.JniMirror
import com.github.klee0kai.thekey.app.engine.NativeLibLoader
import com.github.klee0kai.thekey.app.engine.model.Storage
import com.github.klee0kai.thekey.core.utils.error.FSNoAccessError
import com.github.klee0kai.thekey.core.utils.error.FSNoFileName
import java.io.IOException

@JniMirror
class EditStorageEngine {

    init {
        NativeLibLoader.loadIfNeed()
    }

    external fun findStorageInfo(path: String): Storage?

    external fun createStorage(storage: Storage): Int

    external fun editStorage(storage: Storage): Int

    external fun move(from: String, to: String): Int

    fun throwError(code: Int) = when (code) {
        0 -> Unit
        -2 -> throw FSNoFileName()
        -2 -> throw FSNoFileName()
        -3 -> throw FSNoAccessError()
        else -> throw IOException()
    }
}

