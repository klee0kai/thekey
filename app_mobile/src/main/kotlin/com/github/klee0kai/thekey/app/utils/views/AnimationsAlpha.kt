package com.github.klee0kai.thekey.app.utils.views

import androidx.compose.animation.core.AnimationConstants.DefaultDurationMillis
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

data class TargetAlpha<T>(
    val target: T,
    val alpha: Float = 0f,
)

@Composable
fun animateAlphaAsState(
    boolean: Boolean,
    label: String = "",
) = animateFloatAsState(
    targetValue = if (boolean) 1f else 0f,
    label = label
)

@Composable
fun <T> StateFlow<T>.collectAsStateCrossFaded(
    key: Any? = null,
    context: CoroutineContext = EmptyCoroutineContext
): State<TargetAlpha<T>> {
    val target by collectAsState(key = key ?: this, context = context)
    return animateTargetAlphaAsState(target)
}

@Composable
fun <T> Flow<T>.collectAsStateCrossFaded(
    key: Any?,
    initial: T,
    context: CoroutineContext = EmptyCoroutineContext
): State<TargetAlpha<T>> {
    val target by collectAsState(key = key, initial = initial, context = context)
    return animateTargetAlphaAsState(target)
}

@Composable
fun <T> animateTargetAlphaAsState(target: T): State<TargetAlpha<T>> {
    val targetAlphaState = remember { mutableStateOf(TargetAlpha(target, 1f)) }
    var targetAlpha by targetAlphaState
    var velocity by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(key1 = target) {
        if (targetAlpha.target != target) {
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
            targetAlpha = targetAlpha.copy(target = target, alpha = 0f)
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
