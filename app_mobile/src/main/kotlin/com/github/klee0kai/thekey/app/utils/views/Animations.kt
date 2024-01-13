package com.github.klee0kai.thekey.app.utils.views

import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.compose.ui.unit.Dp

fun Dp.ratioBetween(start: Dp, end: Dp): Float {
    val len = end - start
    val passed = this - start
    return passed / len
}

fun Number.ratioBetween(start: Number, end: Number): Float {
    val len = end.toFloat() - start.toFloat()
    val passed = this.toFloat() - start.toFloat()
    return passed / len
}

fun Float.accelerateDecelerate(): Float =
    AccelerateDecelerateInterpolator().getInterpolation(this)

fun Float.accelerate(): Float =
    AccelerateInterpolator().getInterpolation(this)

fun Float.decelerate(): Float =
    DecelerateInterpolator().getInterpolation(this)


