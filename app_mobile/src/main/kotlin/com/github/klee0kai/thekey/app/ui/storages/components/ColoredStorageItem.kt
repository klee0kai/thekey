@file:OptIn(ExperimentalFoundationApi::class)

package com.github.klee0kai.thekey.app.ui.storages.components

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.wear.compose.material.Icon
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.hardResetToPreview
import com.github.klee0kai.thekey.core.domain.model.ColoredStorage
import com.github.klee0kai.thekey.core.domain.model.isValid
import com.github.klee0kai.thekey.core.ui.devkit.LocalColorScheme
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.views.DebugDarkContentPreview
import com.github.klee0kai.thekey.core.utils.views.toAnnotationString


@Composable
fun ColoredStorageItem(
    modifier: Modifier = Modifier,
    storage: ColoredStorage = ColoredStorage(),
    onClick: () -> Unit = {},
    onLongClick: (() -> Unit)? = null,
    overlayContent: @Composable () -> Unit = {},
) {
    val colorScheme = LocalColorScheme.current
    val pathInputHelper = remember { DI.pathInputHelper() }

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
        val (colorGroupField, pathField, descriptionField, errorIconField) = createRefs()

        Box(
            modifier = Modifier
                .size(2.dp, 24.dp)
                .background(
                    color = storage.colorGroup
                        ?.let { colorScheme.surfaceSchemas.surfaceScheme(it.keyColor).surfaceColor }
                        ?: Color.Transparent,
                    shape = RoundedCornerShape(1.dp)
                )
                .constrainAs(colorGroupField) {
                    start.linkTo(parent.start, 16.dp)
                    top.linkTo(pathField.top, 4.dp)
                }
        )

        Text(
            text = pathShortPath,
            style = MaterialTheme.typography.bodyMedium,
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
                        verticalBias = 0f,
                    )
                }
        )
        Text(
            text = storage.description,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .constrainAs(descriptionField) {
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

        if (!storage.isValid()) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                tint = LocalColorScheme.current.deleteColor,
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

        overlayContent()
    }
}


@OptIn(DebugOnly::class)
@Preview
@Composable
fun ColoredStorageItemPreview() = DebugDarkContentPreview {
    DI.hardResetToPreview()
    ColoredStorageItem(
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
fun ColoredStorageNotValidItemPreview() = DebugDarkContentPreview{
    DI.hardResetToPreview()
    ColoredStorageItem(
        storage = ColoredStorage(
            path = "path",
            name = "name",
            description = "description"
        ),
    )
}