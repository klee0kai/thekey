package com.github.klee0kai.thekey.app.ui.storages.components.popup

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.color.KeyColor
import com.github.klee0kai.thekey.core.ui.devkit.components.buttons.GroupCircle
import com.github.klee0kai.thekey.core.ui.devkit.components.dropdownfields.SimpleSelectPopupMenu
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.common.Dummy
import com.github.klee0kai.thekey.core.utils.views.DebugDarkContentPreview

@Composable
fun StoragePopupMenu(
    modifier: Modifier = Modifier,
    colorGroups: List<ColorGroup> = emptyList(),
    selectedGroupId: Long? = null,
    onEdit: (() -> Unit)? = null,
    onBackup: (() -> Unit)? = null,
    onExport: (() -> Unit)? = null,
    onColorGroupSelected: ((ColorGroup) -> Unit)? = null,
) {
    val theme = LocalTheme.current
    val colorScheme = theme.colorScheme
    val surfaceColor = LocalTheme.current.colorScheme.popupMenu.surfaceColor
    val editText = stringResource(id = R.string.edit)
    val backupText = stringResource(id = R.string.backup)
    val exportText = stringResource(id = R.string.export)

    ConstraintLayout(modifier = modifier) {
        val (menuField) = createRefs()

        if (onColorGroupSelected != null && colorGroups.isNotEmpty()) {
            val (groupsField, shadowField) = createRefs()

            val scrollState = rememberLazyListState()
            val roundStart by animateDpAsState(if (scrollState.canScrollBackward) 0.dp else 16.dp, label = "roundEnd")
            val shadowStartColor by animateColorAsState(if (scrollState.canScrollBackward) surfaceColor else Color.Transparent, label = "shadowStartColor")
            val roundEnd by animateDpAsState(if (scrollState.canScrollForward) 0.dp else 16.dp, label = "roundEnd")
            val shadowEndColor by animateColorAsState(if (scrollState.canScrollForward) surfaceColor else Color.Transparent, label = "shadowEndColor")

            LazyRow(
                modifier = Modifier
                    .sizeIn(maxWidth = 300.dp)
                    .padding(vertical = 8.dp)
                    .background(
                        color = surfaceColor,
                        shape = RoundedCornerShape(
                            topStart = roundStart, bottomStart = roundStart,
                            topEnd = roundEnd, bottomEnd = roundEnd
                        )
                    )
                    .constrainAs(groupsField) {
                        linkTo(
                            start = parent.start,
                            end = parent.end,
                            top = parent.top,
                            bottom = menuField.top,
                            horizontalBias = 0f,
                            verticalBias = 0f,
                        )
                    },
                state = scrollState
            ) {
                item { Spacer(modifier = Modifier.width(1.dp)) }
                colorGroups.forEach { group ->
                    item(group.id) {
                        GroupCircle(
                            modifier = Modifier
                                .padding(
                                    start = 3.dp,
                                    top = 4.dp,
                                    bottom = 4.dp
                                ),
                            name = group.name,
                            colorScheme = colorScheme.surfaceSchemas.surfaceScheme(group.keyColor),
                            checked = group.id == selectedGroupId,
                            onClick = { onColorGroupSelected.invoke(group) },
                        )
                    }
                }
                item { Spacer(modifier = Modifier.width(4.dp)) }
            }

            Spacer(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .background(
                        Brush.linearGradient(
                            0f to shadowStartColor,
                            0.1f to Color.Transparent,
                            0.9f to Color.Transparent,
                            1f to shadowEndColor
                        )
                    )
                    .constrainAs(shadowField) {
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                        linkTo(
                            start = groupsField.start,
                            end = groupsField.end,
                            top = groupsField.top,
                            bottom = groupsField.bottom,
                        )
                    }
            )
        }

        SimpleSelectPopupMenu(
            modifier = Modifier.constrainAs(menuField) {
                linkTo(
                    start = parent.start,
                    end = parent.end,
                    top = parent.top,
                    bottom = parent.bottom,
                    horizontalBias = 1f,
                    verticalBias = 1f,
                )
            },
            variants = buildList {
                if (onEdit != null) add(editText)
                if (onBackup != null) add(backupText)
                if (onExport != null) add(exportText)
            },
            onSelected = { text, _ ->
                when (text) {
                    editText -> onEdit?.invoke()
                    backupText -> onBackup?.invoke()
                    exportText -> onExport?.invoke()
                }
            }
        )
    }


}

@OptIn(DebugOnly::class)
@Preview
@Composable
private fun StoragePopupMenuPreview() = DebugDarkContentPreview {
    Box(modifier = Modifier.background(Color.White)) {
        val selectedGroupId = Dummy.dummyId
        StoragePopupMenu(
            selectedGroupId = selectedGroupId,
            colorGroups = listOf(
                ColorGroup(Dummy.dummyId, "N", KeyColor.PINK),
                ColorGroup(Dummy.dummyId, "A", KeyColor.ORANGE),
                ColorGroup(selectedGroupId, "E", KeyColor.VIOLET),
                ColorGroup(Dummy.dummyId, "E", KeyColor.VIOLET),
                ColorGroup(Dummy.dummyId, "E", KeyColor.VIOLET),
                ColorGroup(Dummy.dummyId, "E", KeyColor.VIOLET),
                ColorGroup(Dummy.dummyId, "E", KeyColor.VIOLET),
            ),
            onEdit = {},
            onBackup = {},
            onExport = {},
            onColorGroupSelected = { },
        )
    }
}

