package com.github.klee0kai.thekey.app.ui.storages.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.github.klee0kai.thekey.app.ui.storages.components.popup.GroupPopupMenu
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.color.KeyColor
import com.github.klee0kai.thekey.core.ui.devkit.components.LazyListIndicatorIfNeed
import com.github.klee0kai.thekey.core.ui.devkit.components.buttons.AddCircle
import com.github.klee0kai.thekey.core.ui.devkit.components.buttons.GroupCircle
import com.github.klee0kai.thekey.core.ui.devkit.components.scrollPosition
import com.github.klee0kai.thekey.core.ui.devkit.overlay.PopupMenu
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.common.Dummy
import com.github.klee0kai.thekey.core.utils.possitions.onGlobalPositionState
import com.github.klee0kai.thekey.core.utils.possitions.rememberViewPosition
import com.github.klee0kai.thekey.core.utils.views.DebugDarkScreenPreview
import com.github.klee0kai.thekey.core.utils.views.animatedBackground
import com.github.klee0kai.thekey.core.utils.views.rememberDerivedStateOf

@Composable
fun GroupsSelectContent(
    modifier: Modifier = Modifier,
    selectedGroup: Long? = null,
    colorGroups: List<ColorGroup> = emptyList(),
    onAdd: () -> Unit = {},
    onGroupSelected: (ColorGroup) -> Unit = {},
    onGroupEdit: (ColorGroup) -> Unit = {},
) {
    val theme = LocalTheme.current
    val safeContentPaddings = WindowInsets.safeContent.asPaddingValues()
    val lazyListState = rememberLazyListState()
    val scrollPosition by rememberDerivedStateOf { lazyListState.scrollPosition() }

    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
    ) {
        val (groupsHint, groupsList, indicator) = createRefs()

        Text(
            text = stringResource(id = R.string.groups),
            style = theme.typeScheme.typography.labelSmall,
            color = theme.colorScheme.androidColorScheme.onSurface.copy(alpha = 0.4f),
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
            pos = scrollPosition,
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
            item("start_spacer") {
                Spacer(modifier = Modifier.width(14.dp))
            }

            colorGroups.forEachIndexed { _, group ->
                item(key = group.id, contentType = group::class) {
                    var showMenu by remember { mutableStateOf(false) }
                    val position = rememberViewPosition()

                    GroupCircle(
                        name = group.name,
                        buttonSize = 56.dp,
                        colorScheme = theme.colorScheme.surfaceSchemas.surfaceScheme(group.keyColor),
                        checked = group.id == selectedGroup,
                        modifier = Modifier
                            .onGlobalPositionState(position)
                            .animatedBackground(showMenu, theme.colorScheme.popupMenu.shadowColor)
                            .padding(
                                start = 1.dp,
                                top = 16.dp,
                                end = 1.dp,
                                bottom = 16.dp
                            ),
                        onLongClick = { showMenu = true },
                        onClick = { onGroupSelected.invoke(group) },
                    )

                    PopupMenu(
                        visible = showMenu,
                        positionAnchor = position,
                        ignoreAnchorSize = true,
                        onDismissRequest = { showMenu = false },
                    ) {
                        GroupPopupMenu(
                            modifier = Modifier.padding(vertical = 4.dp),
                            onEdit = {
                                showMenu = false
                                onGroupEdit(group)
                            }
                        )
                    }
                }
            }

            item("add_button") {
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

            item("end_spacer") {
                Spacer(modifier = Modifier.width(14.dp))
            }
        }
    }
}

@OptIn(DebugOnly::class)
@Preview
@Composable
private fun GroupsSelectContentPreview() = DebugDarkScreenPreview {
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
