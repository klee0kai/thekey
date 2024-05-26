package com.github.klee0kai.thekey.core.utils.views

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayoutScope

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