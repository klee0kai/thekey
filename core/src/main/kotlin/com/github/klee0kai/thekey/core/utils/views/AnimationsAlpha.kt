@file:OptIn(ExperimentalWearMaterialApi::class)

package com.github.klee0kai.thekey.core.utils.views

import androidx.compose.animation.core.AnimationConstants.DefaultDurationMillis
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.SwipeProgress
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration

@Stable
@Immutable
data class TargetAlpha<T>(
    val current: T,
    val next: T,
    val alpha: Float = 0f,
)

fun <T> TargetAlpha<T>.hideOnTargetAlpha(vararg targetsToHide: T): Float {
    return when {
        targetsToHide.any { current == it } -> 0f
        targetsToHide.any { next == it } -> alpha
        else -> 1f
    }
}

fun <T> TargetAlpha<T>.visibleOnTargetAlpha(vararg targetsToVisible: T): Float {
    return when {
        targetsToVisible.any { current == it } -> alpha
        else -> 0f
    }
}

@Composable
inline fun animateAlphaAsState(
    boolean: Boolean,
    animationSpec: AnimationSpec<Float> = spring<Float>(),
    label: String = "",
) = animateFloatAsState(
    targetValue = if (boolean) 1f else 0f,
    animationSpec = animationSpec,
    label = label
)

@Composable
inline fun <T> Flow<T>.collectAsStateCrossFaded(
    key: Any?,
    initial: T,
    skipStates: List<T> = emptyList(),
    context: CoroutineContext = EmptyCoroutineContext,
): State<TargetAlpha<T>> {
    val target by collectAsState(key = key, initial = initial, context = context)
    return animateTargetCrossFaded(target, skipStates = skipStates)
}

@Composable
inline fun <T> rememberTargetCrossFaded(
    skipStates: List<T> = emptyList(),
    noinline calculation: () -> T
): State<TargetAlpha<T>> {
    val target = rememberDerivedStateOf(calculation)
    return animateTargetCrossFaded(target = target.value, skipStates = skipStates)
}

@Composable
inline fun rememberAlphaAnimate(noinline calculation: () -> Boolean): State<Float> {
    val target = rememberDerivedStateOf(calculation)
    return animateAlphaAsState(target.value)
}

fun <T> SwipeProgress<T>.crossFadeAlpha(): TargetAlpha<T> = when {
    fraction < 0.5f -> {
        TargetAlpha(
            current = from,
            next = to,
            alpha = (fraction * 2f).ratioBetween(1f, 0f).coerceIn(0f, 1f)
        )
    }

    else -> {
        TargetAlpha(
            current = to,
            next = to,
            alpha = ((fraction - 0.5f) * 2f).ratioBetween(0f, 1f).coerceIn(0f, 1f)
        )
    }
}

@Composable
inline fun <T> animateTargetCrossFaded(
    target: T,
    skipStates: List<T> = emptyList(),
    delay: Duration = Duration.ZERO,
): State<TargetAlpha<T>> {
    val targetAlphaState = remember { mutableStateOf(TargetAlpha(target, target, 1f)) }
    var targetAlpha by targetAlphaState
    var velocity by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(key1 = target) {
        if (targetAlpha.current in skipStates) {
            targetAlpha = TargetAlpha(target, target, 1f)
            velocity = 0f
            return@LaunchedEffect
        }

        targetAlpha = targetAlpha.copy(next = target)
        if (targetAlpha.current != target) {
            if (delay > Duration.ZERO) delay(delay)

            animate(
                initialValue = targetAlpha.alpha,
                targetValue = 0f,
                initialVelocity = velocity,
                animationSpec = tween(
                    durationMillis = DefaultDurationMillis / 2,
                    easing = FastOutLinearInEasing,
                ),
            ) { newAlpha, newVelocity ->
                targetAlpha = targetAlpha.copy(alpha = newAlpha)
                velocity = newVelocity
            }
            velocity = -velocity
            targetAlpha = targetAlpha.copy(current = target, alpha = 0f)
        }

        animate(
            initialValue = targetAlpha.alpha,
            targetValue = 1f,
            initialVelocity = velocity,
            animationSpec = tween(
                durationMillis = DefaultDurationMillis / 2,
                easing = LinearOutSlowInEasing,
            ),
        ) { newAlpha, newVelocity ->
            targetAlpha = targetAlpha.copy(alpha = newAlpha)
            velocity = newVelocity
        }
        velocity = 0f
    }
    return targetAlphaState
}
