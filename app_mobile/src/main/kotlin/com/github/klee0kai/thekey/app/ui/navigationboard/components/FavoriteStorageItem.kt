package com.github.klee0kai.thekey.app.ui.navigationboard.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.domain.model.ColoredStorage
import com.github.klee0kai.thekey.app.ui.designkit.AppTheme
import com.github.klee0kai.thekey.app.ui.designkit.LocalColorScheme
import com.github.klee0kai.thekey.app.utils.views.toAnnotationString


@Composable
fun FavoriteStorageItem(
    modifier: Modifier = Modifier,
    storage: ColoredStorage = ColoredStorage(
        path = "path",
        name = "name",
        description = "description"
    ),
    onClick: () -> Unit = {}
) {
    val colorScheme = LocalColorScheme.current
    val pathInputHelper = remember { DI.pathInputHelper() }

    val pathShortPath = with(pathInputHelper) {
        storage.path
            .shortPath()
            .toAnnotationString()
            .coloredPath()
    }

    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable(onClick = onClick)
    ) {
        val (pathField, nameField) = createRefs()


        Text(
            text = pathShortPath,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .constrainAs(pathField) {
                    width = Dimension.fillToConstraints
                    linkTo(
                        top = parent.top,
                        bottom = parent.bottom,
                        start = parent.start,
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
            text = storage.name,
            color = Color(0xffB7B7B7),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.W400,
            modifier = Modifier
                .constrainAs(nameField) {
                    width = Dimension.fillToConstraints
                    linkTo(
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


@Preview
@Composable
private fun FavoriteStorageItemPreview() = AppTheme {
    FavoriteStorageItem(
        storage = ColoredStorage(
            path = "phoneStorage/Documents/business.ckey",
            name = "business",
        )
    )
}

@Preview(widthDp = 200)
@Composable
private fun FavoriteStorageItemShortPreview() = AppTheme {
    FavoriteStorageItem(
        storage = ColoredStorage(
            path = "phoneStorage/Documents/business.ckey",
            name = "business",
        )
    )
}

@Preview(widthDp = 250)
@Composable
private fun FavoriteStorageItemShort2Preview() = AppTheme {
    FavoriteStorageItem(
        storage = ColoredStorage(
            path = "phoneStorage/Documents/business.ckey",
            name = "business",
        )
    )
}