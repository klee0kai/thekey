package com.github.klee0kai.thekey.core.utils.views

import android.util.DisplayMetrics
import com.github.klee0kai.thekey.core.di.CoreDI


fun Int.toDp(): Float {
    val ctx = CoreDI.activity() ?: return 0f
    val px = this.toFloat()

    return px / (ctx.resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
}

fun Int.toPx(): Float {
    val ctx = CoreDI.activity() ?: return 0f
    val dp = this.toFloat()

    return dp * (ctx.resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
}