package com.github.klee0kai.thekey.app.engine.twins

import com.github.klee0kai.brooklyn.JniMirror
import com.github.klee0kai.thekey.app.engine.NativeLibLoader
import com.github.klee0kai.thekey.app.engine.model.TwinsCollection

@JniMirror
class PasswordTwinsEngine {

    init {
        NativeLibLoader.loadIfNeed()
    }

    external fun findTwins(passw: String): TwinsCollection?

}