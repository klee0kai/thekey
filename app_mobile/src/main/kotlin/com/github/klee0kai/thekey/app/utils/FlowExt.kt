package com.github.klee0kai.thekey.app.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn

fun <T> Flow<T>.shareLatest(
    scope: CoroutineScope
) = shareIn(scope, SharingStarted.Eagerly, replay = 1)