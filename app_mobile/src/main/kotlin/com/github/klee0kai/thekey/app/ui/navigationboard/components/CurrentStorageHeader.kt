package com.github.klee0kai.thekey.app.ui.navigationboard.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.github.klee0kai.thekey.core.domain.model.ColoredStorage
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.app.R as AppR

@Composable
fun CurrentStorageHeader(
    modifier: Modifier = Modifier,
    storage: ColoredStorage = ColoredStorage(),
) {
    ConstraintLayout(
        modifier = modifier
            .defaultMinSize(minWidth = 200.dp)
    ) {
        val (titleHeader, iconField, storagePathField, storageNameField) = createRefs()

        Text(
            modifier = Modifier
                .constrainAs(titleHeader) {
                    width = Dimension.fillToConstraints
                    linkTo(
                        top = parent.top,
                        bottom = parent.bottom,
                        start = parent.start,
                        end = parent.end,
                        verticalBias = 0f,
                        horizontalBias = 0f,
                        topMargin = 24.dp,
                        startMargin = 16.dp,
                        endMargin = 16.dp,
                    )
                },
            style = MaterialTheme.typography.titleLarge,
            text = stringResource(id = R.string.current_storage)
        )


        Image(
            modifier = Modifier
                .size(48.dp, 48.dp)
                .constrainAs(iconField) {
                    linkTo(
                        top = titleHeader.bottom,
                        bottom = parent.bottom,
                        start = parent.start,
                        end = parent.end,
                        verticalBias = 0f,
                        horizontalBias = 0f,
                        topMargin = 28.dp,
                        startMargin = 4.dp,
                        bottomMargin = 28.dp,
                    )
                },
            painter = painterResource(id = AppR.drawable.key_to_left),
            contentDescription = "key",
        )

        Text(
            modifier = Modifier
                .constrainAs(storagePathField) {
                    width = Dimension.fillToConstraints
                    linkTo(
                        top = iconField.top,
                        bottom = iconField.bottom,
                        start = iconField.end,
                        end = parent.end,
                        startMargin = 8.dp,
                        endMargin = 16.dp,
                        verticalBias = 0f,
                        horizontalBias = 0f,
                    )
                },
            style = MaterialTheme.typography.bodyMedium,
            text = storage.path
        )

        Text(
            modifier = Modifier
                .constrainAs(storageNameField) {
                    width = Dimension.fillToConstraints
                    linkTo(
                        top = storagePathField.bottom,
                        bottom = iconField.bottom,
                        start = iconField.end,
                        end = parent.end,
                        topMargin = 8.dp,
                        startMargin = 8.dp,
                        endMargin = 16.dp,
                        verticalBias = 0f,
                        horizontalBias = 0f,
                        bottomMargin = 24.dp,
                    )
                },
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xffB7B7B7),
            text = storage.name
        )
    }
}


@Preview
@Composable
fun CurrentStorageHeaderPreview() = AppTheme {
    CurrentStorageHeader(
        storage = ColoredStorage(path = "phoneStorage/Documents/pet.ckey", name = "petprojects")
    )
}