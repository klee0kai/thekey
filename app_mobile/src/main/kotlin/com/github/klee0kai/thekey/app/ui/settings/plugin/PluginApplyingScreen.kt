package com.github.klee0kai.thekey.app.ui.settings.plugin

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.ui.designkit.AppTheme

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
                linkTo(
                    start = parent.start,
                    end = parent.end,
                    top = parent.top,
                    bottom = parent.bottom,
                    verticalBias = 0.6f,
                    bottomMargin = 16.dp,
                )
            },
            text = stringResource(id = R.string.installing),
        )
    }
}

@Preview(showSystemUi = true, device = Devices.PIXEL_6)
@Composable
private fun PluginApplyingScreenPreview() = AppTheme {
    PluginApplyingScreen()
}