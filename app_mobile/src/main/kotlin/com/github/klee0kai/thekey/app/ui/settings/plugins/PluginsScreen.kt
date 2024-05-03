package com.github.klee0kai.thekey.app.ui.settings.plugins

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.Text
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.designkit.AppTheme
import com.github.klee0kai.thekey.app.ui.designkit.LocalRouter
import com.github.klee0kai.thekey.app.ui.navigation.model.PluginDestination
import com.github.klee0kai.thekey.app.utils.views.collectAsState
import com.github.klee0kai.thekey.app.utils.views.rememberOnScreenRef

@Composable
fun PluginsScreen() {
    val scope = rememberCoroutineScope()
    val router = LocalRouter.current
    val presenter by rememberOnScreenRef { DI.pluginsPresenter() }
    val features by presenter!!.features.collectAsState(key = Unit, initial = emptyList())

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        features.forEach { feature ->
            item {
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        router.navigate(PluginDestination(feature = feature.feature.moduleName))
                    }) {
                    Text(text = stringResource(id = feature.feature.titleRes))
                }
            }
        }
    }
}


@Preview(
    showSystemUi = true,
    device = Devices.PIXEL_6,
)
@Composable
private fun PluginsScreenPreview() = AppTheme {
    PluginsScreen()
}