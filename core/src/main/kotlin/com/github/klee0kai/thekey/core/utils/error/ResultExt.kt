package com.github.klee0kai.thekey.core.utils.error

import timber.log.Timber

inline fun <reified T> Result<T>.logError() = onFailure { Timber.d(it) }

inline fun <reified T> Result<T>.fatalError() = onFailure { Timber.wtf(it) }