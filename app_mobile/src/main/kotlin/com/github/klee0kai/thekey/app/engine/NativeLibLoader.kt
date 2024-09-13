@file:OptIn(InternalCoroutinesApi::class)

package com.github.klee0kai.thekey.app.engine

import com.github.klee0kai.brooklyn.Brooklyn
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

object NativeLibLoader {

    var loaded = false
        private set

    fun loadIfNeed() {
        synchronized(this) {
            if (!loaded) {
                Brooklyn.loadLibrary("crypt-storage-lib")
                loaded = true
            }
        }
    }

}