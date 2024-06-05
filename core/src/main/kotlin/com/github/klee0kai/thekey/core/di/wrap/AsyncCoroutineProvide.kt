package com.github.klee0kai.thekey.core.di.wrap

import com.github.klee0kai.stone.wrappers.Ref
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class AsyncCoroutineProvide<T>(
    provider: suspend () -> T
) {

    constructor(call: Ref<T>) : this(provider = { call.get() })

    @OptIn(DelicateCoroutinesApi::class)
    private val asyncValue =
        GlobalScope.async(Dispatchers.Default) {
            provider.invoke()
        }

    suspend fun get(): T = asyncValue.await()

    suspend operator fun invoke(): T = asyncValue.await()

}