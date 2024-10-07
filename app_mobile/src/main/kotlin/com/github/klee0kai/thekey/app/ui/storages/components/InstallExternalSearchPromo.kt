package com.github.klee0kai.thekey.app.ui.storages.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.theme.DefaultThemes
import com.github.klee0kai.thekey.core.utils.views.linkToParent
import com.github.klee0kai.thekey.core.R as CoreR


@Composable
fun InstallExternalSearchPromo(
    modifier: Modifier = Modifier,
) {
    val theme = LocalTheme.current
    ConstraintLayout(
        modifier = modifier.fillMaxSize()
    ) {
        val (titleField, hintField) = createRefs()

        Text(
            text = stringResource(id = CoreR.string.install_storage_search),
            style = theme.typeScheme.header,
            modifier = Modifier.constrainAs(titleField) {
                linkToParent(
                    startMargin = 16.dp,
                    endMargin = 16.dp,
                    verticalBias = 0.2f,
                )
            }
        )

        Text(
            text = stringResource(id = CoreR.string.install_storage_search_hint),
            style = theme.typeScheme.body,
            color = theme.colorScheme.textColors.hintTextColor,
            modifier = Modifier
                .constrainAs(hintField) {
                    linkToParent(
                        top = titleField.bottom,
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
fun InstallExternalSearchPromoPreview() = AppTheme(theme = DefaultThemes.darkTheme) {
    InstallExternalSearchPromo()
}