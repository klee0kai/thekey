@file:OptIn(ExperimentalFoundationApi::class)

package com.github.klee0kai.thekey.app.ui.simpleboard.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.hardResetToPreview
import com.github.klee0kai.thekey.core.domain.model.ColoredStorage
import com.github.klee0kai.thekey.core.ui.devkit.LocalColorScheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.views.DebugDarkContentPreview
import com.github.klee0kai.thekey.core.utils.views.linkToParent
import com.github.klee0kai.thekey.core.utils.views.toAnnotationString

@Composable
fun FavoriteStorageElement(
    modifier: Modifier = Modifier,
    storage: ColoredStorage = ColoredStorage(),
    onClick: () -> Unit = {},
    onLongClick: (() -> Unit)? = null,
) {
    val theme = LocalTheme.current
    val colorScheme = LocalColorScheme.current
    val pathInputHelper = remember { DI.pathInputHelper() }
    val isDescNotEmpty = storage.description.isNotBlank() || storage.name.isNotBlank()

    val pathShortPath = with(pathInputHelper) {
        storage.path
            .shortPath()
            .toAnnotationString()
            .coloredPath(accentColor = colorScheme.androidColorScheme.primary)
            .coloredFileExt(extensionColor = theme.colorScheme.textColors.hintTextColor)
    }

    ConstraintLayout(
        modifier = modifier
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            )
            .defaultMinSize(minHeight = 56.dp)
            .wrapContentHeight()
            .fillMaxWidth(),
    ) {
        val (pathField, nameField) = createRefs()


        Text(
            text = pathShortPath,
            style = theme.typeScheme.body,
            modifier = Modifier
                .constrainAs(pathField) {
                    width = Dimension.fillToConstraints
                    linkToParent(
                        topMargin = 6.dp,
                        bottomMargin = 6.dp,
                        startMargin = 16.dp,
                        endMargin = 16.dp,
                        horizontalBias = 0f,
                        verticalBias = 0f,
                    )
                }
        )

        if (isDescNotEmpty) {
            Text(
                text = when {
                    storage.name.isNotBlank() && storage.description.isNotBlank() -> "${storage.name}  ~  ${storage.description}"
                    else -> "${storage.name}${storage.description}"
                },
                style = theme.typeScheme.bodySmall,
                color = theme.colorScheme.textColors.hintTextColor,
                modifier = Modifier
                    .constrainAs(nameField) {
                        width = Dimension.fillToConstraints
                        linkToParent(
                            top = pathField.bottom,
                            bottom = parent.bottom,
                            start = parent.start,
                            end = parent.end,
                            topMargin = 4.dp,
                            startMargin = 16.dp,
                            endMargin = 16.dp,
                            bottomMargin = 6.dp,
                            horizontalBias = 0f,
                            verticalBias = 1f,
                        )
                    }
            )
        }
    }
}


@OptIn(DebugOnly::class)
@Preview(widthDp = 250)
@Composable
private fun FavoriteStorageItemPreview() {
    DI.hardResetToPreview()
    DebugDarkContentPreview {
        FavoriteStorageElement(
            storage = ColoredStorage(
                path = "/phoneStorage/Documents/business.ckey",
                name = "business",
            )
        )
    }
}


@OptIn(DebugOnly::class)
@Preview(widthDp = 450)
@Composable
private fun FavoriteStorageItemNoDescPreview() {
    DI.hardResetToPreview()
    DebugDarkContentPreview {
        FavoriteStorageElement(
            storage = ColoredStorage(
                path = "/phoneStorage/Documents/business.ckey",
            )
        )
    }
}