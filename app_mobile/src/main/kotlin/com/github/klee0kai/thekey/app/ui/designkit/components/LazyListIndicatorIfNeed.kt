package com.github.klee0kai.thekey.app.ui.designkit.components

import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.github.klee0kai.thekey.app.ui.designkit.AppTheme
import com.github.klee0kai.thekey.app.utils.views.animateAlphaAsState
import com.github.klee0kai.thekey.app.utils.views.ratioBetween
import kotlinx.parcelize.Parcelize

@Parcelize
data class ScrollPosition(
    val listVisibleRatio: Float = 0f,
    val scrollRatio: Float = 0f,
) : Parcelable

fun LazyListState.scrollPosition(): ScrollPosition {
    val allItemsCounts = layoutInfo.totalItemsCount
    val visibleItemsCount = layoutInfo.visibleItemsInfo.size
    val listVisibleRatio =
        visibleItemsCount
            .ratioBetween(0, allItemsCounts)
            .coerceIn(0f, 1f)

    val scrollRatio = firstVisibleItemIndex
        .ratioBetween(start = 0, end = allItemsCounts - visibleItemsCount)
        .coerceIn(0f, 1f)

    return ScrollPosition(
        listVisibleRatio = listVisibleRatio,
        scrollRatio = scrollRatio,
    )
}

@Composable
fun LazyListIndicatorIfNeed(
    modifier: Modifier = Modifier,
    forceVisible: Boolean = false,
    horizontal: Boolean = false,
    pos: ScrollPosition = ScrollPosition(),
) {

    val scrollableListVisible by animateAlphaAsState(boolean = forceVisible || pos.listVisibleRatio > 0.01f && pos.listVisibleRatio < 0.99f)

    if (scrollableListVisible == 0f) return

    val indicatorModifier = if (horizontal) {
        Modifier
            .fillMaxHeight()
            .fillMaxWidth(pos.listVisibleRatio)
    } else {
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(pos.listVisibleRatio)
    }

    ConstraintLayout(
        modifier = modifier
            .alpha(scrollableListVisible)
    ) {
        val (indicatorContainer, indicator) = createRefs()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(4.dp)
                )
                .constrainAs(indicatorContainer) {
                    linkTo(
                        start = parent.start,
                        end = parent.end,
                        top = parent.top,
                        bottom = parent.bottom,
                    )
                }
        )

        Box(
            modifier = indicatorModifier
                .background(
                    color = MaterialTheme.colorScheme.inverseSurface,
                    shape = RoundedCornerShape(2.dp)
                )
                .constrainAs(indicator) {
                    linkTo(
                        start = indicatorContainer.start,
                        end = indicatorContainer.end,
                        top = indicatorContainer.top,
                        bottom = indicatorContainer.bottom,
                        horizontalBias = pos.scrollRatio,
                    )
                }
        )
    }

}

@Preview
@Composable
private fun ListIndicatorVerticalPreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .size(10.dp, 100.dp)
        ) {
            LazyListIndicatorIfNeed(
                modifier = Modifier
                    .size(2.dp, 48.dp)
                    .align(Alignment.Center),
                pos = ScrollPosition(
                    listVisibleRatio = 0.5f,
                    scrollRatio = 0.2f,
                )
            )
        }
    }
}

@Preview
@Composable
private fun ListIndicatorHorizontalPreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .size(100.dp, 10.dp)
        ) {
            LazyListIndicatorIfNeed(
                horizontal = true,
                modifier = Modifier
                    .size(48.dp, 2.dp)
                    .align(Alignment.Center),
                pos = ScrollPosition(
                    listVisibleRatio = 0.7f,
                    scrollRatio = 0.9f,
                )
            )
        }
    }
}


@Preview
@Composable
private fun ListIndicatorZeroPreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .size(100.dp, 10.dp)
        ) {
            LazyListIndicatorIfNeed(
                modifier = Modifier
                    .size(48.dp, 2.dp)
                    .align(Alignment.Center),
            )
        }
    }
}