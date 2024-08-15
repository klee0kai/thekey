package com.github.klee0kai.thekey.app.engine.twins

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.engine.model.TwinsCollection
import kotlinx.coroutines.withContext

class PasswordTwinsSuspended {

    private val _engine = DI.passwordTwinsEngine()
    private val dispatcher = DI.jniDispatcher()

    suspend fun findTwins(
        passw: String,
    ): TwinsCollection? = engine {
        findTwins(passw)
    }


    private suspend fun <T> engine(
        block: suspend PasswordTwinsEngine.() -> T,
    ): T = withContext(dispatcher) {
        _engine().block()
    }

}