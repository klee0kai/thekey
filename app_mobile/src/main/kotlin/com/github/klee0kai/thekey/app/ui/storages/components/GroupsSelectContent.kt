@file:OptIn(ExperimentalFoundationApi::class)

package com.github.klee0kai.thekey.app.ui.storages.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalColorScheme
import com.github.klee0kai.thekey.app.ui.designkit.components.LazyListIndicatorIfNeed
import com.github.klee0kai.thekey.app.ui.designkit.components.buttons.AddCircle
import com.github.klee0kai.thekey.app.ui.designkit.components.buttons.GroupCircle
import com.github.klee0kai.thekey.app.ui.designkit.components.scrollPosition
import com.github.klee0kai.thekey.core.ui.devkit.color.KeyColor
import com.github.klee0kai.thekey.core.utils.common.Dummy

@Composable
fun GroupsSelectContent(
    modifier: Modifier = Modifier,
    selectedGroup: Long? = null,
    colorGroups: List<ColorGroup> = emptyList(),
    onAdd: () -> Unit = {},
    onGroupSelected: (ColorGroup) -> Unit = {},
    onGroupEdit: (ColorGroup) -> Unit = {},
    onGroupDelete: (ColorGroup) -> Unit = {},
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
            fontWeight = FontWeight.Bold,
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
            pos = lazyListState.scrollPosition(),
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
            item {
                Spacer(modifier = Modifier.width(14.dp))
            }

            colorGroups.forEachIndexed { index, group ->
                item(key = group.id) {
                    var showMenu by remember { mutableStateOf(false) }

                    GroupCircle(
                        name = group.name,
                        buttonSize = 56.dp,
                        colorScheme = colorScheme.surfaceScheme(group.keyColor),
                        checked = group.id == selectedGroup,
                        modifier = Modifier
                            .padding(
                                start = 1.dp,
                                top = 16.dp,
                                end = 1.dp,
                                bottom = 16.dp
                            ),
                        onLongClick = { showMenu = true },
                        onClick = { onGroupSelected.invoke(group) },
                        overlayContent = {
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                GroupDropDownMenuContent(
                                    onEdit = { onGroupEdit(group) },
                                    onDelete = { onGroupDelete(group) },
                                )
                            }
                        }
                    )
                }
            }

            item {
                AddCircle(
                    modifier = Modifier
                        .padding(
                            start = 1.dp,
                            top = 16.dp,
                            end = 1.dp,
                            bottom = 16.dp
                        ),
                    buttonSize = 56.dp,
                    onClick = onAdd
                )
            }

            item {
                Spacer(modifier = Modifier.width(14.dp))
            }
        }
    }
}

@Preview
@Composable
private fun GroupsSelectContentPreview() = AppTheme {
    GroupsSelectContent(
        selectedGroup = 1L,
        colorGroups = listOf(
            ColorGroup(Dummy.dummyId, "CE", KeyColor.CORAL),
            ColorGroup(Dummy.dummyId, "AN", KeyColor.ORANGE),
            ColorGroup(Dummy.dummyId, "TU", KeyColor.TURQUOISE),
            ColorGroup(Dummy.dummyId, "T", KeyColor.VIOLET),
        ),
    )
}
