package com.github.klee0kai.thekey.app.utils.views

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.utils.common.ObjHolder
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
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

@Composable
fun <T> accumulate(init: T, calculation: (old: T) -> T): State<T> {
    val state = remember { mutableStateOf(init) }
    val newState = calculation(state.value)
    if (newState != state.value) {
        state.value = calculation(state.value)
    }
    return state
}

@Composable
fun <T> rememberDerivedStateOf(calculation: () -> T) = remember {
    derivedStateOf(calculation)
}

@Composable
@NonRestartableComposable
inline fun <T> rememberOnScreen(block: () -> T): T {
    val cached = remember { ObjHolder<T?>(null) }
    DisposableEffect(key1 = Unit) {
        onDispose {
            cached.value = null
        }
    }
    return when {
        cached.value != null -> cached.value as T
        else -> block().also { cached.value = it }
    }
}


@Composable
@NonRestartableComposable
inline fun <T> rememberOnScreenRef(block: () -> T): WeakReference<T> {
    return WeakReference(rememberOnScreen(block))
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
    val isSkeletonAnimated by rememberTargetCrossFaded { isSkeleton() }
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

fun Modifier.thenIf(condition: Boolean, block: Modifier.() -> Modifier) =
    if (condition) block() else this