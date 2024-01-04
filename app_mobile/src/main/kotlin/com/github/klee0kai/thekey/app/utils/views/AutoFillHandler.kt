package com.github.klee0kai.thekey.app.utils.views

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
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
fun ViewPositionPx.toDp() =
    with(LocalDensity.current) {
        ViewPositionDp(
            globalPos = DpOffset(globalPos.x.toDp(), globalPos.y.toDp()),
            size = DpSize(size.width.toDp(), size.height.toDp())
        )
    }

fun Modifier.belowAfter(
    position: ViewPositionDp,
    topMargin: Dp = 0.dp,
): Modifier {
    return absoluteOffset(
        x = position.globalPos.x,
        y = position.globalPos.y + position.size.height + topMargin,
    ).width(position.size.width)
}

@Preview
@Composable
fun AutoFillList(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    variants: List<String> = listOf("dsd", "ds", "dsd"),
    onSelected: (String?) -> Unit = {},
) {
    val variantsListAlpha by animateFloatAsState(
        targetValue = if (isVisible && variants.isNotEmpty()) 1f else 0f,
        label = "variants visible animate"
    )

    if (variantsListAlpha > 0) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding()
                .alpha(variantsListAlpha)
        ) {
            LazyColumn(
                modifier = modifier
                    .heightIn(0.dp, 200.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                variants.forEach { text ->
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelected.invoke(text)
                                }
                        ) {
                            Text(
                                text = text,
                                modifier = Modifier
                                    .padding(all = 8.dp)
                                    .padding(start = 8.dp, end = 8.dp)
                                    .fillMaxWidth()
                            )
                        }

                    }
                }
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}