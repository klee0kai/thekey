package com.github.klee0kai.thekey.core.utils.error

import kotlinx.coroutines.CoroutineExceptionHandler
import timber.log.Timber
import kotlin.reflect.KClass

fun Throwable.causes() = generateSequence(this) { it.cause }

inline fun <reified T : Throwable> Throwable.cause(cl: KClass<T>) =
    causes().firstOrNull { it.javaClass == cl.java } as? T

inline fun <reified T : Throwable> Throwable.cause() =
    causes().firstOrNull { it.javaClass == T::class.java } as? T

inline fun <reified T : Throwable> Throwable.isCause(cl: KClass<T>) = cause(cl) != null

object CoroutineHandlers {
    val debugHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Timber.d(throwable)
    }

    val warningHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Timber.w(throwable)
    }

    val errorHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Timber.w(throwable)
    }

    val wtfHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Timber.wtf(throwable)
    }
}