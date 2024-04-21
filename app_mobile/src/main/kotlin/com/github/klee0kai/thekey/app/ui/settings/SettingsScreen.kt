package com.github.klee0kai.thekey.app.ui.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.Text
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.ui.designkit.AppTheme
import com.github.klee0kai.thekey.app.ui.designkit.LocalRouter
import com.github.klee0kai.thekey.app.ui.navigation.model.PluginsDestination

@Composable
fun SettingScreen() {
    val router = LocalRouter.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        item {
            TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { router.navigate(PluginsDestination) }) {
                Text(text = stringResource(id = R.string.plugins))
            }
        }
    }
}


@Preview(
    showSystemUi = true,
    device = Devices.PIXEL_6,
)
@Composable
private fun SettingsScreenPreview() = AppTheme {
    SettingScreen()
}