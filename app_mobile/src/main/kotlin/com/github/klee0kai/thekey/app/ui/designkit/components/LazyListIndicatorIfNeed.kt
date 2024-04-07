package com.github.klee0kai.thekey.app.ui.designkit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.github.klee0kai.thekey.app.utils.views.ratioBetween
import com.github.klee0kai.thekey.app.utils.views.rememberDerivedStateOf


@Composable
@Preview
fun LazyListIndicatorIfNeed(
    modifier: Modifier = Modifier,
    horizontal: Boolean = false,
    forceScrollIndicator: Boolean = false,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    val allItemsCounts by rememberDerivedStateOf { lazyListState.layoutInfo.totalItemsCount }
    val visibleItemsCount by rememberDerivedStateOf { lazyListState.layoutInfo.visibleItemsInfo.size }
    val isScrollIndicatorVisible by rememberDerivedStateOf { forceScrollIndicator || allItemsCounts > visibleItemsCount * 1.3f }
    if (!isScrollIndicatorVisible) return
    val firstVisibleItem by rememberDerivedStateOf { lazyListState.firstVisibleItemIndex }
    val listVisibleRatio by rememberDerivedStateOf {
        visibleItemsCount
            .ratioBetween(0, allItemsCounts)
            .coerceIn(0f, 1f)
    }
    val scrollRatio by rememberDerivedStateOf {
        firstVisibleItem
            .ratioBetween(start = 0, end = allItemsCounts - visibleItemsCount)
            .coerceIn(0f, 1f)
    }

    ConstraintLayout(
        modifier = modifier
    ) {
        val (indicatorContainer, indicator) = createRefs()

        val indicatorModifier by rememberDerivedStateOf {
            if (horizontal) {
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(listVisibleRatio)
            } else {
                Modifier
                    .fillMaxWidth()
                    .fillMaxWidth(listVisibleRatio)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(2.dp)
                )
                .constrainAs(indicatorContainer) {
                    linkTo(
                        start = parent.start,
                        end = parent.end,
                        top = parent.bottom,
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
                        horizontalBias = scrollRatio,
                    )
                }
        )
    }

}