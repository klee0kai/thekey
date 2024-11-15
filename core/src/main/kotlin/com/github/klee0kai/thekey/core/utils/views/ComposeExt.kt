package com.github.klee0kai.thekey.core.utils.views

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.core.di.CoreDI
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.utils.common.Cleanable
import com.github.klee0kai.thekey.core.utils.common.ObjHolder
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

@Deprecated("use rememberOnScreenRef")
@Composable
@NonRestartableComposable
inline fun <T> rememberOnScreen(block: () -> T): T {
    val cached = remember { ObjHolder<T?>(null) }
    DisposableEffect(key1 = Unit) {
        onDispose {
            (cached.value as? Cleanable)?.clean()
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
    isSkeleton: Boolean = true,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    color: Color = LocalTheme.current.colorScheme.skeletonColor,
): Modifier {
    val shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.Window)
    if (!isSkeleton) return this

    return this then shimmer(shimmer)
        .background(
            color = color,
            shape = shape,
        )
}

@Composable
fun animateSkeletonModifier(
    color: Color = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.4f),
    shape: RoundedCornerShape = RoundedCornerShape(4.dp),
    isSkeleton: () -> Boolean,
): State<Modifier> {
    val shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.Window)
    val isSkeletonAnimated by rememberTargetFaded { isSkeleton() }
    return rememberDerivedStateOf {
        val modifier = Modifier.alpha(isSkeletonAnimated.alpha)
        if (!isSkeletonAnimated.current) {
            modifier
        } else {
            modifier
                .shimmer(shimmer)
                .background(
                    color = color,
                    shape = shape,
                )
        }
    }
}

@Composable
fun Modifier.animateContentSizeProduction() = run {
    if (CoreDI.config().isViewEditMode) this
    else animateContentSize()
}

inline fun Modifier.ifProduction(block: Modifier.() -> Modifier) =
    if (!CoreDI.config().isViewEditMode) block() else this

inline fun <reified Res, reified T : Res> T.thenIf(
    condition: Boolean,
    block: T.() -> Res,
) = if (condition) block() else this


@Composable
fun Modifier.thenIfCrossFade(
    condition: Boolean,
    block: @Composable Modifier.() -> Modifier,
): Modifier {
    val target by animateTargetFaded(target = condition)
    return if (target.current) {
        block().alpha(target.alpha)
    } else {
        this.alpha(target.alpha)
    }
}

fun Modifier.tappable(onTap: ((Offset) -> Unit)? = null) =
    this.pointerInput(Unit) { detectTapGestures(onTap = onTap) }

@Composable
fun Modifier.animatedBackground(condition: Boolean, background: Color): Modifier {
    val alpha by animateAlphaAsState(boolean = condition)
    return this.background(background.copy(alpha = background.alpha * alpha))
}

val <T> CompositionLocal<T>.currentRef
    @Composable get() = rememberOnScreenRef { current }