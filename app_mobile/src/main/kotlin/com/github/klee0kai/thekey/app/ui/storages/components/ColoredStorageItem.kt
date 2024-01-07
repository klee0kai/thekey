package com.github.klee0kai.thekey.app.ui.storages.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.model.ColoredStorage


@Preview
@Composable
fun ColoredStorageItem(
    storage: ColoredStorage = ColoredStorage(),
    onClick: () -> Unit = {}
) {
    val colorScheme = remember { DI.theme().colorScheme() }
    val userShortPaths = remember { DI.userShortPaths() }
    val storage = if (LocalView.current.isInEditMode) {
        ColoredStorage(path = "path", name = "name", description = "description")
    } else {
        storage
    }
    val pathShortPath = (if (!LocalView.current.isInEditMode) {
        userShortPaths.shortPathName(storage.path)
    } else {
        storage.path
    }).let {
        userShortPaths.colorTransformation
            .filter(AnnotatedString(it))
            .text
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable(onClick = onClick)
    ) {
        val (colorGroup, path, description) = createRefs()

        Box(
            modifier = Modifier
                .size(2.dp, 24.dp)
                .background(
                    color = storage.colorGroup
                        ?.let { colorScheme.surfaceScheme(it).surfaceColor }
                        ?: Color.Transparent,
                    shape = RoundedCornerShape(1.dp)
                )
                .constrainAs(colorGroup) {
                    start.linkTo(parent.start, 16.dp)
                    top.linkTo(path.top, 4.dp)
                }
        )

        Text(
            text = pathShortPath,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .constrainAs(path) {
                    linkTo(
                        top = parent.top,
                        bottom = parent.bottom,
                        start = colorGroup.end,
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
                .constrainAs(description) {
                    linkTo(
                        top = path.bottom,
                        bottom = parent.bottom,
                        start = colorGroup.end,
                        end = parent.end,
                        topMargin = 4.dp,
                        startMargin = 16.dp,
                        endMargin = 16.dp,
                        bottomMargin = 6.dp,
                        horizontalBias = 0f,
                        verticalBias = 1f,
                    )
                    top.linkTo(path.bottom, margin = 4.dp)
                }
        )
    }
}