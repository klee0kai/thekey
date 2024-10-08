package com.github.klee0kai.thekey.core.di.debug

import com.github.klee0kai.thekey.core.di.modules.CoroutineModule
import kotlinx.coroutines.Dispatchers

class DummyCoroutineMainTreadModule(
    val origin: CoroutineModule
) : CoroutineModule by origin {

    override fun ioDispatcher() = Dispatchers.Main.immediate

    override fun defaultDispatcher() = Dispatchers.Main.immediate

    override fun jniDispatcher() = Dispatchers.Main.immediate

}