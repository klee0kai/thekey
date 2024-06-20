package com.github.klee0kai.thekey.core.utils.possitions

import android.view.View
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min

fun View.matchViewPosition() = ViewPositionPx(
    globalPos = IntOffset(0, 0),
    size = IntSize(width = width, height = height)
)

fun ViewPositionDp.leftOf(containerSize: DpSize, vararg positions: ViewPositionDp): ViewPositionDp {
    var myPos = copy(globalPos = globalPos.copy(x = containerSize.width))
    positions.forEach {
        myPos = myPos.copy(
            globalPos = globalPos.copy(
                x = min(myPos.globalPos.x, it.globalPos.x - myPos.size.width),
            ),
        )
    }
    positions.forEach {
        myPos = myPos.copy(
            size = myPos.size.copy(
                width = min(myPos.size.width, myPos.globalPos.x),
            )
        )
    }
    return myPos
}


fun ViewPositionDp.rightOf(
    containerSize: DpSize,
    vararg positions: ViewPositionDp,
): ViewPositionDp {
    var myPos = this
    positions.forEach {
        myPos = myPos.copy(
            globalPos = globalPos.copy(
                x = max(myPos.globalPos.x, it.globalPos.x + it.size.width),
            ),
        )
        myPos = myPos.copy(
            size = myPos.size.copy(
                width = min(myPos.size.width, containerSize.width - myPos.globalPos.x),
            )
        )
    }
    return myPos
}


fun ViewPositionDp.topOf(containerSize: DpSize, vararg positions: ViewPositionDp): ViewPositionDp {
    var myPos = copy(globalPos = globalPos.copy(y = containerSize.height))
    positions.forEach {
        myPos = myPos.copy(
            globalPos = globalPos.copy(
                y = min(myPos.globalPos.y, it.globalPos.y - myPos.size.height),
            ),
        )
    }
    positions.forEach {
        myPos = myPos.copy(
            size = myPos.size.copy(
                height = min(myPos.size.height, myPos.globalPos.y),
            )
        )
    }
    return myPos
}


fun ViewPositionDp.bottomOf(
    containerSize: DpSize,
    vararg positions: ViewPositionDp,
): ViewPositionDp {
    var myPos = this
    positions.forEach {
        myPos = myPos.copy(
            globalPos = globalPos.copy(
                y = max(myPos.globalPos.y, it.globalPos.y + it.size.height),
            ),
        )
        myPos = myPos.copy(
            size = myPos.size.copy(
                height = min(myPos.size.height, containerSize.height - myPos.globalPos.y),
            )
        )
    }
    return myPos
}