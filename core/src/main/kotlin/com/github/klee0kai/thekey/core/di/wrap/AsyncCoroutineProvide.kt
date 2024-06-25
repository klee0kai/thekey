package com.github.klee0kai.thekey.core.di.wrap

import com.github.klee0kai.stone.wrappers.Ref
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class AsyncCoroutineProvide<T>(
    private val provider: suspend () -> T
) {

    constructor(call: Ref<T>) : this(provider = { call.get() })

    @OptIn(DelicateCoroutinesApi::class)
    private val asyncValue =
        GlobalScope.async(Dispatchers.Default + errorHandler) {
            provider.invoke()
        }

    suspend fun get(): T = asyncValue.await()

    suspend operator fun invoke(): T = asyncValue.await()

    fun syncGet() = runBlocking { provider.invoke() }

    companion object {
        private val errorHandler = CoroutineExceptionHandler { _, exception ->
            Timber.wtf(exception, "resolve dep error")
        }
    }

}