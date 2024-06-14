package com.github.klee0kai.thekey.core.utils.views

import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.compose.ui.unit.Dp
import kotlin.math.abs

fun Dp.ratioBetween(start: Dp, end: Dp): Float {
    val len = end - start
    val passed = this - start
    return passed / len
}

fun Number.ratioBetween(start: Number, end: Number): Float {
    val len = end.toFloat() - start.toFloat()
    val passed = this.toFloat() - start.toFloat()
    if (abs(len) < 1e-9f) return 0f
    return passed / len
}

fun Float.progressTo(start: Dp, end: Dp): Dp {
    val distance = end - start
    return start + distance * this
}

fun Float.accelerateDecelerate(): Float =
    AccelerateDecelerateInterpolator().getInterpolation(this)

fun Float.accelerate(): Float =
    AccelerateInterpolator().getInterpolation(this)

fun Float.decelerate(): Float =
    DecelerateInterpolator().getInterpolation(this)


