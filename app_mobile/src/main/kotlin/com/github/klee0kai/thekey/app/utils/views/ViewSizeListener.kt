package com.github.klee0kai.thekey.app.utils.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

data class ViewPositionPx(
    val globalPos: IntOffset,
    val size: IntSize,
)

data class ViewPositionDp(
    val globalPos: DpOffset,
    val size: DpSize,
)

fun Modifier.onGlobalPositionState(
    onChange: (ViewPositionPx) -> Unit
) = onGloballyPositioned { layoutCoordinates ->
    if (layoutCoordinates.isAttached) {
        val state = ViewPositionPx(
            globalPos = layoutCoordinates
                .positionInRoot()
                .run { IntOffset(x.roundToInt(), y.roundToInt()) },
            size = layoutCoordinates.size
        )
        onChange.invoke(state)
    }
}

@Composable
fun ViewPositionPx.toDp() = with(LocalDensity.current) {
    ViewPositionDp(
        globalPos = DpOffset(globalPos.x.toDp(), globalPos.y.toDp()),
        size = DpSize(size.width.toDp(), size.height.toDp())
    )
}

@Composable
fun IntSize.pxToDp() = with(LocalDensity.current) {
    DpSize(width.toDp(), height.toDp())
}


@Composable
fun Float.pxToDp(): Dp {
    val px = this
    return with(LocalDensity.current) { px.toDp() }
}

@Composable
fun Int.pxToDp(): Dp {
    val px = this
    return with(LocalDensity.current) { px.toDp() }
}

@Composable
fun Dp.toPx(): Float {
    val dp = this
    return with(LocalDensity.current) { dp.toPx() }
}


@Composable
fun currentViewSizeState(): State<DpSize> {
    val view = LocalView.current
    var sizePx by remember { mutableStateOf(IntSize(0, 0)) }
    val stateDp = remember { mutableStateOf(DpSize(0.dp, 0.dp)) }
    sizePx = IntSize(view.width, view.height)

    LaunchedEffect(Unit) {
        view.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            val newSizePx = IntSize(view.width, view.height)
            if (sizePx != newSizePx) sizePx = newSizePx
        }
    }

    stateDp.value = sizePx.pxToDp()
    return stateDp
}