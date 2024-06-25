package com.github.klee0kai.thekey.core.utils.possitions

import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

data class ViewPositionPx(
    val globalPos: IntOffset = IntOffset(0, 0),
    val size: IntSize = IntSize(0, 0),
)

data class ViewPositionDp(
    val globalPos: DpOffset = DpOffset(0.dp, 0.dp),
    val size: DpSize = DpSize(0.dp, 0.dp),
)