package com.github.klee0kai.thekey.dynamic.findstorage.ui.storages.content

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.dynamic.findstorage.R


@Composable
fun RequestExternalStoragePermissionsContent() {
    val theme = LocalTheme.current
    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val (titleField, hintField) = createRefs()

        Text(
            text = stringResource(id = R.string.grant_rights),
            style = theme.typeScheme.typography.labelMedium,
            modifier = Modifier.constrainAs(titleField) {
                linkTo(
                    top = parent.top,
                    bottom = parent.bottom,
                    start = parent.start,
                    end = parent.end,
                    startMargin = 16.dp,
                    endMargin = 16.dp,
                    verticalBias = 0.2f,
                )
            }
        )

        Text(
            text = stringResource(id = R.string.grant_rights_hint),
            style = theme.typeScheme.typography.labelSmall,
            modifier = Modifier
                .alpha(0.4f)
                .constrainAs(hintField) {
                    linkTo(
                        top = titleField.bottom,
                        bottom = parent.bottom,
                        start = parent.start,
                        end = parent.end,
                        startMargin = 16.dp,
                        verticalBias = 0f,
                        endMargin = 16.dp,
                        topMargin = 10.dp,
                    )
                }
        )

    }
}


@Composable
@Preview(device = Devices.PHONE)
fun RequestExternalStoragePermissionsPreview() = AppTheme {
    RequestExternalStoragePermissionsContent()
}