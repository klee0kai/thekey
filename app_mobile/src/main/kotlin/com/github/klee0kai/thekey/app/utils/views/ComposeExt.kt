package com.github.klee0kai.thekey.app.utils.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@Composable
@NonRestartableComposable
fun <T : R, R> Deferred<T>.collectAsState(
    initial: R,
    context: CoroutineContext = EmptyCoroutineContext
): State<R> = produceState(initial, this, context) {
    if (context == EmptyCoroutineContext) {
        value = await()
    } else withContext(context) {
        value = await()
    }
}

@Composable
@NonRestartableComposable
fun <T> rememberDerivedStateOf(calculation: () -> T) = remember {
    derivedStateOf(calculation)
}