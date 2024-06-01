@file:OptIn(ExperimentalLayoutApi::class)

package com.github.klee0kai.thekey.core.utils.views

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.max


val WindowInsets.bottomDp
    @Composable
    get() = getBottom(LocalDensity.current).pxToDp()

val WindowInsets.topDp
    @Composable
    get() = getTop(LocalDensity.current).pxToDp()

val WindowInsets.startDp
    @Composable
    get() = getLeft(LocalDensity.current, LocalLayoutDirection.current).pxToDp()

val WindowInsets.endDp
    @Composable
    get() = getRight(LocalDensity.current, LocalLayoutDirection.current).pxToDp()


val WindowInsets.Companion.isIme: Boolean
    @Composable
    get() = if (LocalInspectionMode.current) false else isImeVisible

fun WindowInsets.minInsets(all: Dp) = minInsets(all, all, all, all)

fun WindowInsets.minInsets(vertical: Dp, horizontal: Dp) = minInsets(
    top = vertical,
    bottom = vertical,
    right = horizontal,
    left = horizontal
)

fun PaddingValues.horizontal(minValue: Dp = 0.dp) =
    maxOf(calculateLeftPadding(LayoutDirection.Ltr), minValue)

fun WindowInsets.minInsets(
    top: Dp,
    bottom: Dp,
    left: Dp,
    right: Dp,
) = object : WindowInsets {
    private val original = this@minInsets
    override fun getBottom(density: Density): Int = with(density) {
        max(original.getBottom(density), bottom.roundToPx())
    }

    override fun getLeft(density: Density, layoutDirection: LayoutDirection): Int = with(density) {
        max(original.getLeft(density, layoutDirection), left.roundToPx())
    }

    override fun getRight(density: Density, layoutDirection: LayoutDirection): Int = with(density) {
        max(original.getRight(density, layoutDirection), right.roundToPx())
    }

    override fun getTop(density: Density): Int = with(density) {
        max(original.getTop(density), top.roundToPx())
    }
}

fun WindowInsets.truncate(
    top: Boolean = false,
    bottom: Boolean = false,
    left: Boolean = false,
    right: Boolean = false,
) = object : WindowInsets {
    private val original = this@truncate
    override fun getBottom(density: Density): Int =
        if (!bottom) original.getBottom(density) else 0

    override fun getLeft(density: Density, layoutDirection: LayoutDirection): Int =
        if (!left) original.getLeft(density, layoutDirection) else 0

    override fun getRight(density: Density, layoutDirection: LayoutDirection): Int =
        if (!right) original.getRight(density, layoutDirection) else 0

    override fun getTop(density: Density): Int =
        if (!top) original.getTop(density) else 0
}


