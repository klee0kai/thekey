@file:OptIn(ExperimentalFoundationApi::class)

package com.github.klee0kai.thekey.app.ui.designkit.components.buttons

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.ui.designkit.AppTheme
import com.github.klee0kai.thekey.app.ui.designkit.color.SurfaceScheme

@Composable
fun GroupCircle(
    modifier: Modifier = Modifier,
    buttonSize: Dp = 48.dp,
    name: String = "",
    colorScheme: SurfaceScheme,
    checked: Boolean = false,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    overlayContent: @Composable () -> Unit = {},
) {
    val checkedState by animateDpAsState(if (checked) 12.dp else buttonSize / 2, label = "color group checked")
    val rotate by animateFloatAsState(targetValue = if (checked) 70f else 0f, label = "color group select")

    Box(
        modifier = modifier
            .size(buttonSize * 1.17f),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .minimumInteractiveComponentSize()
                .rotate(rotate)
                .size(buttonSize)
                .background(color = colorScheme.surfaceColor, shape = RoundedCornerShape(checkedState))
                .clip(RoundedCornerShape(checkedState))
                .align(Alignment.Center)
                .run {
                    when {
                        onClick == null && onLongClick == null -> this
                        else -> combinedClickable(
                            onLongClick = onLongClick,
                            onClick = { onClick?.invoke() }
                        )
                    }
                },
        )

        Text(
            modifier = Modifier.align(Alignment.Center),
            color = colorScheme.onSurfaceColor,
            text = name
        )

        overlayContent()
    }
}


@Preview
@Composable
private fun GroupCirclePreview() {
    AppTheme {
        GroupCircle(
            name = "AN",
            colorScheme = SurfaceScheme(Color.Magenta, Color.White),
        )
    }
}


@Preview
@Composable
private fun GroupCircleCheckedPreview() {
    AppTheme {
        GroupCircle(
            name = "CH",
            colorScheme = SurfaceScheme(Color.Magenta, Color.White),
            checked = true,
        )
    }
}


