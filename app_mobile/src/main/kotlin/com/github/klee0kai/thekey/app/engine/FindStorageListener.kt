package com.github.klee0kai.thekey.app.engine

import com.github.klee0kai.brooklyn.JniMirror
import com.github.klee0kai.thekey.app.model.Storage

@JniMirror
abstract class FindStorageListener {
    open fun onStorageFound(storage: Storage) {}
}