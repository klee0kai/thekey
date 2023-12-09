package com.github.klee0kai.thekey.app.ui.designkit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import com.github.klee0kai.thekey.app.utils.views.ratioBetween


@Composable
@Preview
fun LazyListIndicatorIfNeed(
    modifier: Modifier = Modifier,
    horizontal: Boolean = false,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    val allItemsCounts = remember { derivedStateOf { lazyListState.layoutInfo.totalItemsCount } }
    val visibleItemsCount = remember(allItemsCounts.value) {
        lazyListState.layoutInfo.visibleItemsInfo.size
    }
    val isScrollIndicatorVisible = allItemsCounts.value > visibleItemsCount * 1.3f
    if (!isScrollIndicatorVisible) return
    val firstVisibleItem = remember { derivedStateOf { lazyListState.firstVisibleItemIndex } }
    val listVisibleRatio = visibleItemsCount.ratioBetween(0, allItemsCounts.value)
    val scrollRatio = firstVisibleItem.value.ratioBetween(
        start = 0,
        end = allItemsCounts.value - visibleItemsCount
    )

    ConstraintLayout(
        modifier = modifier
    ) {
        val (indicatorContainer, indicator) = createRefs()

        val indicatorModifier = if (horizontal) {
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(listVisibleRatio)
        } else {
            Modifier
                .fillMaxWidth()
                .fillMaxWidth(listVisibleRatio)
        }


        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.4f))
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
                .background(MaterialTheme.colorScheme.inverseSurface)
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