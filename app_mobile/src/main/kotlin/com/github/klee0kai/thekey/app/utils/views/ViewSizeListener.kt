package com.github.klee0kai.thekey.app.utils.views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
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
fun ViewPositionPx.toDp() =
    with(LocalDensity.current) {
        ViewPositionDp(
            globalPos = DpOffset(globalPos.x.toDp(), globalPos.y.toDp()),
            size = DpSize(size.width.toDp(), size.height.toDp())
        )
    }