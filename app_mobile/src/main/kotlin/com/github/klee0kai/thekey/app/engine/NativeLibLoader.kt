package com.github.klee0kai.thekey.app.engine

import com.github.klee0kai.brooklyn.Brooklyn
import java.util.concurrent.atomic.AtomicBoolean

object NativeLibLoader {

    private var loaded = AtomicBoolean(false)

    fun loadIfNeed() {
        if (!loaded.getAndSet(true)) {
            Brooklyn.loadLibrary("crypt-storage-lib")
        }
    }

}