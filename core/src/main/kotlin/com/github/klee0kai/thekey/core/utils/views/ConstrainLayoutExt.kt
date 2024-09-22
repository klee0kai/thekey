package com.github.klee0kai.thekey.core.utils.views

import androidx.annotation.FloatRange
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstrainScope
import androidx.constraintlayout.compose.ConstraintLayoutBaseScope

fun ConstrainScope.linkToParent(
    start: ConstraintLayoutBaseScope.VerticalAnchor = parent.start,
    top: ConstraintLayoutBaseScope.HorizontalAnchor = parent.top,
    end: ConstraintLayoutBaseScope.VerticalAnchor = parent.end,
    bottom: ConstraintLayoutBaseScope.HorizontalAnchor = parent.bottom,
    startMargin: Dp = 0.dp,
    topMargin: Dp = 0.dp,
    endMargin: Dp = 0.dp,
    bottomMargin: Dp = 0.dp,
    startGoneMargin: Dp = 0.dp,
    topGoneMargin: Dp = 0.dp,
    endGoneMargin: Dp = 0.dp,
    bottomGoneMargin: Dp = 0.dp,
    @FloatRange(from = 0.0, to = 1.0) horizontalBias: Float = 0.5f,
    @FloatRange(from = 0.0, to = 1.0) verticalBias: Float = 0.5f
) {
    linkTo(
        start = start,
        top = top,
        bottom = bottom,
        end = end,
        startMargin = startMargin,
        topMargin = topMargin,
        endMargin = endMargin,
        bottomMargin = bottomMargin,
        startGoneMargin = startGoneMargin,
        topGoneMargin = topGoneMargin,
        endGoneMargin = endGoneMargin,
        bottomGoneMargin = bottomGoneMargin,
        horizontalBias = horizontalBias,
        verticalBias = verticalBias,
    )
}