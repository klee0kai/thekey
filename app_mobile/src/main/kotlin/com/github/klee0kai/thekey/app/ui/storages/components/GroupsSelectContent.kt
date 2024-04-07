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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.domain.model.ColorGroup
import com.github.klee0kai.thekey.app.ui.designkit.AppTheme
import com.github.klee0kai.thekey.app.ui.designkit.LocalColorScheme
import com.github.klee0kai.thekey.app.ui.designkit.color.KeyColor
import com.github.klee0kai.thekey.app.ui.designkit.components.GroupCircle
import com.github.klee0kai.thekey.app.ui.designkit.components.LazyListIndicatorIfNeed
import com.github.klee0kai.thekey.app.utils.common.DummyId

@Composable
fun GroupsSelectContent(
    modifier: Modifier = Modifier,
    selectedGroup: Long? = null,
    colorGroups: List<ColorGroup> = emptyList(),
    forceScrollIndicator: Boolean = false,
    onAdd: () -> Unit = {},
    onGroupSelected: (ColorGroup) -> Unit = {},
) {
    val colorScheme = LocalColorScheme.current
    val lazyListState = rememberLazyListState()

    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
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
            }
        )

        LazyListIndicatorIfNeed(
            lazyListState = lazyListState,
            horizontal = true,
            forceScrollIndicator = forceScrollIndicator,
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
                })
        {
            colorGroups.forEachIndexed { index, group ->
                item(key = group.id) {
                    val isFirst = index == 0

                    GroupCircle(
                        name = group.name,
                        colorScheme = colorScheme.surfaceScheme(group.keyColor),
                        checked = group.id == selectedGroup,
                        modifier = Modifier
                            .padding(
                                start = if (isFirst) 16.dp else 8.dp,
                                top = 16.dp,
                                end = 4.dp,
                                bottom = 16.dp
                            ),
                        onClick = { onGroupSelected.invoke(group) }
                    )
                }
            }

            item {
                val isFirst = colorGroups.isEmpty()
                AddCircle(
                    modifier = Modifier
                        .padding(
                            start = if (isFirst) 16.dp else 8.dp,
                            top = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp
                        ),
                    onClick = onAdd
                )
            }
        }
    }
}

@Preview
@Composable
private fun GroupsSelectContentPreview() {
    AppTheme {
        GroupsSelectContent(
            forceScrollIndicator = true,
            selectedGroup = 1L,
            colorGroups = listOf(
                ColorGroup(DummyId.dummyId, "CE", KeyColor.CORAL),
                ColorGroup(DummyId.dummyId, "AN", KeyColor.ORANGE),
                ColorGroup(DummyId.dummyId, "TU", KeyColor.TURQUOISE),
                ColorGroup(DummyId.dummyId, "T", KeyColor.VIOLET),
            ),
        )
    }
}
