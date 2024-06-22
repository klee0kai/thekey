@file:OptIn(ExperimentalFoundationApi::class)

package com.github.klee0kai.thekey.dynamic.findstorage.ui.storages.content

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.wear.compose.material.Icon
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.hardResetToPreview
import com.github.klee0kai.thekey.core.domain.model.ColoredStorage
import com.github.klee0kai.thekey.core.domain.model.isValid
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.color.KeyColor
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.views.DebugDarkContentPreview
import com.github.klee0kai.thekey.core.utils.views.toAnnotationString


@Composable
fun FSColoredStorageItem(
    modifier: Modifier = Modifier,
    storage: ColoredStorage = ColoredStorage(),
    onClick: () -> Unit = {},
    onLongClick: (() -> Unit)? = null,
) {
    val theme = LocalTheme.current
    val colorScheme = theme.colorScheme
    val pathInputHelper = remember { DI.pathInputHelper() }
    val isDescNotEmpty = storage.description.isNotBlank() || storage.name.isNotBlank()

    val pathShortPath = with(pathInputHelper) {
        storage.path
            .shortPath()
            .toAnnotationString()
            .coloredPath(accentColor = colorScheme.androidColorScheme.primary)
    }

    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            )
    ) {
        val (colorGroupField, pathField, nameField, errorIconField) = createRefs()

        Box(
            modifier = Modifier
                .size(2.dp, 24.dp)
                .background(
                    color = colorScheme.surfaceSchemas.surfaceScheme(storage.colorGroup?.keyColor ?: KeyColor.NOCOLOR).surfaceColor,
                    shape = RoundedCornerShape(1.dp)
                )
                .constrainAs(colorGroupField) {
                    linkTo(
                        top = parent.top,
                        bottom = parent.bottom,
                        start = parent.start,
                        end = parent.end,
                        topMargin = 12.dp,
                        bottomMargin = 12.dp,
                        startMargin = 16.dp,
                        endMargin = 16.dp,
                        horizontalBias = 0f,
                        verticalBias = 0.5f,
                    )
                }
        )

        Text(
            text = pathShortPath,
            style = theme.typeScheme.typography.bodyMedium,
            modifier = Modifier
                .constrainAs(pathField) {
                    linkTo(
                        top = parent.top,
                        bottom = parent.bottom,
                        start = colorGroupField.end,
                        end = parent.end,
                        topMargin = 6.dp,
                        bottomMargin = 6.dp,
                        startMargin = 16.dp,
                        endMargin = 16.dp,
                        horizontalBias = 0f,
                        verticalBias = if (isDescNotEmpty) 0f else 0.5f,
                    )
                }
        )

        if (isDescNotEmpty) {
            Text(
                text = when {
                    storage.name.isNotBlank() && storage.description.isNotBlank() -> "${storage.name}  ~  ${storage.description}"
                    else -> "${storage.name}${storage.description}"
                },
                style = theme.typeScheme.typography.bodySmall,
                modifier = Modifier
                    .constrainAs(nameField) {
                        linkTo(
                            top = pathField.bottom,
                            bottom = parent.bottom,
                            start = colorGroupField.end,
                            end = parent.end,
                            topMargin = 4.dp,
                            startMargin = 16.dp,
                            endMargin = 16.dp,
                            bottomMargin = 6.dp,
                            horizontalBias = 0f,
                            verticalBias = 1f,
                        )
                        top.linkTo(pathField.bottom, margin = 4.dp)
                    }
            )
        }

        if (!storage.isValid()) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                tint = colorScheme.deleteColor,
                modifier = Modifier.constrainAs(errorIconField) {
                    linkTo(
                        top = parent.top,
                        bottom = parent.bottom,
                        start = parent.start,
                        end = parent.end,
                        startMargin = 16.dp,
                        endMargin = 16.dp,
                        horizontalBias = 1f,
                    )
                }
            )
        }
    }
}


@OptIn(DebugOnly::class)
@Preview
@Composable
fun FSColoredStorageItemPreview() = DebugDarkContentPreview {
    DI.hardResetToPreview()
    FSColoredStorageItem(
        storage = ColoredStorage(
            path = "path",
            name = "name",
            description = "description",
            version = 1,
        ),
    )
}

@OptIn(DebugOnly::class)
@Preview
@Composable
fun FSColoredStorageNoDescItemPreview() = DebugDarkContentPreview {
    DI.hardResetToPreview()
    FSColoredStorageItem(
        storage = ColoredStorage(
            path = "path",
            version = 1,
        ),
    )
}


@OptIn(DebugOnly::class)
@Preview
@Composable
fun FSColoredStorageNotValidItemPreview() = DebugDarkContentPreview {
    DI.hardResetToPreview()
    FSColoredStorageItem(
        storage = ColoredStorage(
            path = "path",
            name = "name",
            description = "description"
        ),
    )
}

@OptIn(DebugOnly::class)
@Preview
@Composable
fun FSColoredStorageDescItemPreview() = DebugDarkContentPreview {
    DI.hardResetToPreview()
    FSColoredStorageItem(
        storage = ColoredStorage(
            path = "path",
            description = "description"
        ),
    )
}