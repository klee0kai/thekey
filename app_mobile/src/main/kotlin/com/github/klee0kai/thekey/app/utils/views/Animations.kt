package com.github.klee0kai.thekey.app.utils.views

import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.compose.animation.core.animate
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.delay

fun Dp.ratioBetween(start: Dp, end: Dp): Float {
    val len = end - start
    val passed = this - start
    return passed / len
}


fun Float.accelerateDecelerate(): Float =
    AccelerateDecelerateInterpolator().getInterpolation(this)

fun Float.accelerate(): Float =
    AccelerateInterpolator().getInterpolation(this)

fun Float.decelerate(): Float =
    DecelerateInterpolator().getInterpolation(this)


suspend fun fadeOutInAnimate(
    reverse: Boolean = false,
    alpha1Init: Float,
    alpha2Init: Float,
    block: (alpha1: Float, alpha2: Float) -> Unit,
) {
    animate(
        if (!reverse) alpha1Init else alpha2Init,
        0f
    ) { value, _ ->
        if (!reverse) {
            block(value.accelerate(), 0f)
        } else {
            block(0f, value.accelerate())
        }
    }
    delay(50)
    animate(0f, 1f) { value, _ ->
        if (!reverse) {
            block(0f, value.decelerate())
        } else {
            block(value.decelerate(), 0f)
        }
    }
}