package com.github.klee0kai.thekey.app.utils.views

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp


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

@Composable
fun Modifier.safeContentPadding(
    top: Boolean = true,
    bottom: Boolean = true,
    start: Boolean = true,
    end: Boolean = true,
): Modifier = padding(
    top = if (top) WindowInsets.safeContent.topDp else 0.dp,
    bottom = if (bottom) WindowInsets.safeContent.bottomDp else 0.dp,
    start = if (start) WindowInsets.safeContent.startDp else 0.dp,
    end = if (end) WindowInsets.safeContent.endDp else 0.dp,
)
