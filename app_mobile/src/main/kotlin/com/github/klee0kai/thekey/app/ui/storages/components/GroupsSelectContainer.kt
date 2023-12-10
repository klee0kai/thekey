package com.github.klee0kai.thekey.app.ui.storages.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.ui.designkit.components.LazyListIndicatorIfNeed
import com.github.klee0kai.thekey.app.ui.designkit.components.SimpleBottomSheetScaffoldState
import com.github.klee0kai.thekey.app.utils.views.accelerateDecelerate
import com.github.klee0kai.thekey.app.utils.views.ratioBetween


@Preview
@Composable
fun GroupsSelectContainer(
    modifier: Modifier = Modifier,
    scaffoldState: SimpleBottomSheetScaffoldState? = null
) {
    val lazyListState = rememberLazyListState()

    val topContentAlpha = scaffoldState?.dragProgress?.floatValue
        ?.ratioBetween(0.3f, 0.7f)
        ?.coerceIn(0f, 1f)
        ?.accelerateDecelerate()
        ?: 1f

    val dragTranslateY = scaffoldState?.dragProgress?.floatValue
        ?.ratioBetween(1f, 0f)
        ?.coerceIn(0f, 1f)
        ?.accelerateDecelerate()
        ?.let { 30.dp * -it }
        ?: 0.dp

    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .alpha(topContentAlpha)
    ) {
        val (groupsHint, groupsList, indicator) = createRefs()

        Text(
            text = stringResource(id = R.string.groups),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            modifier = Modifier.constrainAs(groupsHint) {
                linkTo(
                    start = parent.start,
                    top = parent.top,
                    bottom = groupsList.top,
                    end = parent.end,
                    horizontalBias = 0f,
                    startMargin = 16.dp,
                    verticalBias = 1f,
                )
                translationY = dragTranslateY
            }
        )

        LazyListIndicatorIfNeed(
            lazyListState = lazyListState,
            horizontal = true,
            modifier = Modifier
                .size(52.dp, 4.dp)
                .constrainAs(indicator) {
                    linkTo(
                        start = parent.start,
                        end = parent.end,
                        top = groupsList.bottom,
                        bottom = parent.bottom,
                        verticalBias = 0f,
                    )
                    translationY = dragTranslateY
                },
        )

        LazyRow(
            state = lazyListState,
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .constrainAs(groupsList) {
                    linkTo(
                        top = parent.top,
                        start = parent.start,
                        bottom = parent.bottom,
                        end = parent.end,
                        verticalBias = 0.6f
                    )
                    translationY = dragTranslateY
                })
        {
            val list = (0..20).toList()
            list.forEachIndexed { index, i ->
                val isFirst = index == 0
                val isLast = list.lastIndex == index
                item {
                    GroupCircle(
                        modifier = Modifier
                            .padding(
                                start = if (isFirst) 16.dp else 4.dp,
                                top = 16.dp,
                                end = if (isLast) 16.dp else 4.dp,
                                bottom = 16.dp
                            )
                    )
                }
            }
        }
    }
}



