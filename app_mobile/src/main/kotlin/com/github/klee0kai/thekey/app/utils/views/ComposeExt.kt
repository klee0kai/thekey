package com.github.klee0kai.thekey.app.utils.views

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.di.DI
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

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun <T : R, R> Flow<T>.collectAsState(
    key: Any?,
    initial: R,
    context: CoroutineContext = EmptyCoroutineContext
): State<R> = produceState((this as? StateFlow)?.value ?: initial, key, context) {
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
fun <T> rememberDerivedStateOf(calculation: () -> T) = remember {
    derivedStateOf(calculation)
}


@Composable
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
fun Modifier.skeleton(
    isSkeleton: Boolean,
    color: Color = MaterialTheme.colorScheme.inverseSurface,
): Modifier {
    val shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.Window)
    if (!isSkeleton) return this

    return this then shimmer(shimmer)
        .background(
            color = color,
            shape = RoundedCornerShape(4.dp)
        )
}

@Composable
fun rememberSkeletonModifier(
    color: Color = MaterialTheme.colorScheme.inverseSurface,
    isSkeleton: () -> Boolean,
): State<Modifier> {
    val shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.Window)
    val isSkeletonAnimated by rememberTargetAlphaCrossSade { isSkeleton() }
    return rememberDerivedStateOf {
        val modifier = Modifier.alpha(isSkeletonAnimated.alpha)
        if (!isSkeletonAnimated.current) {
            modifier
        } else {
            modifier
                .shimmer(shimmer)
                .background(
                    color = color,
                    shape = RoundedCornerShape(4.dp)
                )
        }
    }
}

@Composable
fun Modifier.animateContentSizeProduction() = run {
    if (DI.config().isViewEditMode) this
    else animateContentSize()
}