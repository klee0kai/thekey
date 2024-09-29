package com.github.klee0kai.thekey.core.utils.views

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayoutScope
import com.github.klee0kai.thekey.core.ui.devkit.bottomsheet.SimpleScaffoldConst.dragHandleSize

@Composable
fun ConstraintLayoutScope.createAnchor(
    horizontalBias: Float,
    verticalBias: Float,
): ConstrainedLayoutReference {
    val center = createRef()

    Spacer(modifier = Modifier
        .size(0.dp)
        .constrainAs(center) {
            linkTo(
                start = parent.start,
                end = parent.end,
                top = parent.top,
                bottom = parent.bottom,
                verticalBias = verticalBias,
                horizontalBias = horizontalBias,
            )
        }
    )
    return center
}

@Composable
@NonRestartableComposable
inline fun ConstraintLayoutScope.createDialogBottomAnchor(
    sheetPeekHeight: Dp = 400.dp,
    dragProcess: Float,
    bottomMargin: Dp = 0.dp,
): ConstrainedLayoutReference {
    val (currentBottom) = createRefs()

    Spacer(modifier = Modifier
        .size(0.dp)
        .constrainAs(currentBottom) {
            linkTo(
                start = parent.start,
                end = parent.end,
                top = parent.top,
                bottom = parent.bottom,
                verticalBias = (1f - dragProcess).coerceIn(0f..1f),
                horizontalBias = 0.5f,
                topMargin = sheetPeekHeight - bottomMargin - dragHandleSize,
                bottomMargin = bottomMargin,
            )
        }
    )
    return currentBottom
}