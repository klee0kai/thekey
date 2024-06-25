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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.wear.compose.material.Icon
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.hardResetToPreview
import com.github.klee0kai.thekey.core.domain.model.ColoredStorage
import com.github.klee0kai.thekey.core.domain.model.isValid
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.color.KeyColor
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.views.DebugDarkContentPreview
import com.thedeanda.lorem.LoremIpsum
import java.io.File


@Composable
fun ColoredStorageItem(
    modifier: Modifier = Modifier,
    storage: ColoredStorage = ColoredStorage(),
    onClick: () -> Unit = {},
    onLongClick: (() -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null,
) {
    val theme = LocalTheme.current
    val colorScheme = theme.colorScheme

    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            )
    ) {
        val (colorGroupField, nameField, descriptionField, iconField) = createRefs()

        Box(
            modifier = Modifier
                .size(2.dp, 24.dp)
                .background(
                    color = colorScheme.surfaceSchemas.surfaceScheme(
                        storage.colorGroup?.keyColor ?: KeyColor.NOCOLOR
                    ).surfaceColor,
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
            text = when {
                storage.name.isNotBlank() -> storage.name
                else -> File(storage.path).nameWithoutExtension
            },
            style = theme.typeScheme.typography.bodyMedium,
            modifier = Modifier
                .constrainAs(nameField) {
                    width = Dimension.fillToConstraints
                    linkTo(
                        top = parent.top,
                        bottom = parent.bottom,
                        start = colorGroupField.end,
                        end = iconField.start,
                        topMargin = 6.dp,
                        bottomMargin = 6.dp,
                        startMargin = 16.dp,
                        endMargin = 16.dp,
                        horizontalBias = 0f,
                        verticalBias = if (storage.description.isNotBlank()) 0f else 0.5f,
                    )
                }
        )

        if (storage.description.isNotBlank()) {
            Text(
                text = storage.description,
                style = theme.typeScheme.typography.bodySmall,
                modifier = Modifier
                    .constrainAs(descriptionField) {
                        width = Dimension.fillToConstraints
                        linkTo(
                            top = nameField.bottom,
                            bottom = parent.bottom,
                            start = colorGroupField.end,
                            end = iconField.start,
                            topMargin = 4.dp,
                            startMargin = 16.dp,
                            endMargin = 16.dp,
                            bottomMargin = 6.dp,
                            horizontalBias = 0f,
                            verticalBias = 1f,
                        )
                        top.linkTo(nameField.bottom, margin = 4.dp)
                    }
            )
        }

        Box(modifier = Modifier.constrainAs(iconField) {
            linkTo(
                top = parent.top,
                bottom = parent.bottom,
                start = parent.start,
                end = parent.end,
                startMargin = 16.dp,
                endMargin = 16.dp,
                horizontalBias = 1f,
            )
        }) {
            when {
                icon != null -> icon.invoke()
                !storage.isValid() -> {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = colorScheme.deleteColor,
                    )
                }
            }
        }
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
fun ColoredStorageNoDescItemPreview() = DebugDarkContentPreview {
    DI.hardResetToPreview()
    ColoredStorageItem(
        storage = ColoredStorage(
            path = "path",
            name = "name",
            version = 1,
        ),
    )
}

@OptIn(DebugOnly::class)
@Preview
@Composable
fun ColoredStorageNoValidItemPreview() = DebugDarkContentPreview {
    DI.hardResetToPreview()
    ColoredStorageItem(
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
fun ColoredStorageLargeItemPreview() = DebugDarkContentPreview {
    DI.hardResetToPreview()
    ColoredStorageItem(
        storage = ColoredStorage(
            path = "/some/file.ext",
            name = LoremIpsum().getWords(10, 15),
            description = LoremIpsum().getWords(10, 15),
        ),
        icon = {
            Icon(
                modifier = Modifier,
                imageVector = Icons.Default.Check,
                contentDescription = "Added"
            )
        }
    )
}