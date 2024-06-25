package com.github.klee0kai.thekey.core.utils.possitions

import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.NonRestartableComposable
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
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
@NonRestartableComposable
fun rememberViewPosition() = remember { mutableStateOf<ViewPositionPx?>(null) }


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

fun Modifier.onGlobalPositionState(state: MutableState<in ViewPositionPx>) = onGlobalPositionState { state.value = it }

fun Modifier.placeTo(viewPositionDp: ViewPositionDp) = this
    .size(width = viewPositionDp.size.width, height = viewPositionDp.size.height)
    .absoluteOffset(x = viewPositionDp.globalPos.x, y = viewPositionDp.globalPos.y)


@Composable
fun ViewPositionPx.toDp() = toDp(LocalDensity.current)

fun ViewPositionPx.toDp(density: Density) = with(density) {
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