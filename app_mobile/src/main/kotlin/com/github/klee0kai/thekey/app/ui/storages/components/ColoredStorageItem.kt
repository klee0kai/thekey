package com.github.klee0kai.thekey.app.ui.storages.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.model.ColoredStorage


@Preview
@Composable
fun ColoredStorageItem(
    storage: ColoredStorage = ColoredStorage()
) {
    val colorScheme = remember { DI.theme().colorScheme() }
    val storage = if (LocalView.current.isInEditMode) {
        ColoredStorage(path = "path", name = "name", description = "description")
    } else storage

    ConstraintLayout(
        modifier = Modifier
            .padding(top = 6.dp, bottom = 6.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        val (colorGroup, path, description) = createRefs()

        Box(
            modifier = Modifier
                .size(2.dp, 24.dp)
                .background(
                    color = colorScheme.surfaceScheme(storage.colorGroup).surfaceColor,
                    shape = RoundedCornerShape(1.dp)
                )
                .constrainAs(colorGroup) {
                    start.linkTo(parent.start, 16.dp)
                    top.linkTo(path.top, 4.dp)
                }
        )

        Text(
            text = storage.path,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .constrainAs(path) {
                    linkTo(
                        start = colorGroup.end,
                        end = parent.end,
                        startMargin = 16.dp,
                        endMargin = 16.dp,
                        bias = 0f
                    )
                }
        )
        Text(
            text = storage.description,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .constrainAs(description) {
                    linkTo(
                        start = colorGroup.end,
                        end = parent.end,
                        startMargin = 16.dp,
                        endMargin = 16.dp,
                        bias = 0f
                    )
                    top.linkTo(path.bottom, margin = 4.dp)
                }
        )
    }
}