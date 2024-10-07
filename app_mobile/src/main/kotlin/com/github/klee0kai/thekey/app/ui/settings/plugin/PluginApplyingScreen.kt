package com.github.klee0kai.thekey.app.ui.settings.plugin

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.theme.DefaultThemes
import com.github.klee0kai.thekey.core.utils.views.linkToParent
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate

@Composable
fun PluginApplyingScreen(
    modifier: Modifier = Modifier,
) {
    ConstraintLayout(
        modifier = modifier.fillMaxSize(),
    ) {
        val (
            titleField,
        ) = createRefs()

        Text(
            modifier = Modifier.constrainAs(titleField) {
                linkToParent(
                    verticalBias = 0.6f,
                    bottomMargin = 16.dp,
                )
            },
            text = stringResource(id = R.string.installing),
        )
    }
}

@VisibleForTesting
@Preview(device = Devices.PHONE)
@Composable
fun PluginApplyingScreenPreview() = EdgeToEdgeTemplate {
    AppTheme(theme = DefaultThemes.darkTheme) {
        PluginApplyingScreen()
    }
}