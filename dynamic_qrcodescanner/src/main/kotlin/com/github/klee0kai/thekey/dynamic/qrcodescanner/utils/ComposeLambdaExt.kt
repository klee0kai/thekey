package com.github.klee0kai.thekey.dynamic.qrcodescanner.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.remember


@Composable
@NonRestartableComposable
fun <R> rememberSuspendLambda(
    key1: Any? = null,
    key2: Any? = null,
    key3: Any? = null,
    key4: Any? = null,
    key5: Any? = null,
    key6: Any? = null,
    launch: suspend () -> R,
): suspend () -> R = remember(key1, key2, key3, key4, key5, key6) {
    launch
}
