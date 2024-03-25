package com.github.klee0kai.thekey.app.utils.views

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
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
fun <T : R, R> Flow<T>.collectAsState(
    key: Any?,
    initial: R,
    context: CoroutineContext = EmptyCoroutineContext
): State<R> = produceState(initial, key, context) {
    if (context == EmptyCoroutineContext) {
        collect { value = it }
    } else withContext(context) {
        collect { value = it }
    }
}

@Suppress("StateFlowValueCalledInComposition")
@Composable
fun <T> StateFlow<T>.collectAsState(
    key: Any?,
    context: CoroutineContext = EmptyCoroutineContext
): State<T> = collectAsState(key = key, initial = value, context = context)

@Composable
@NonRestartableComposable
fun <T> rememberDerivedStateOf(calculation: () -> T) = remember {
    derivedStateOf(calculation)
}


@Composable
@NonRestartableComposable
fun rememberTickerOf(trigger: () -> Boolean): State<Int> {
    var lastState by remember { mutableStateOf(trigger()) }
    val updateTicker = remember { mutableIntStateOf(0) }
    val newState = trigger()
    if (lastState != newState) {
        lastState = newState
        if (newState) {
            updateTicker.value++
        }
    }
    return updateTicker
}


@Composable
@NonRestartableComposable
fun Modifier.skeleton(
    color: Color = MaterialTheme.colorScheme.inverseSurface,
    isSkeleton: () -> Boolean,
): Modifier {
    val shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.Window)
    if (!isSkeleton()) return this

    return this then shimmer(shimmer)
        .background(
            color = color,
            shape = RoundedCornerShape(4.dp)
        )

}