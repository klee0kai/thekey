package com.github.klee0kai.thekey.core.utils.error

import kotlin.reflect.KClass

fun Throwable.causes() = generateSequence(this) { it.cause }

inline fun <reified T : Throwable> Throwable.cause(cl: KClass<T>) = causes().firstOrNull { it.javaClass == cl.java } as? T

inline fun <reified T : Throwable> Throwable.cause() = causes().firstOrNull { it.javaClass == T::class.java } as? T

inline fun <reified T : Throwable> Throwable.isCause(cl: KClass<T>) = cause(cl) != null