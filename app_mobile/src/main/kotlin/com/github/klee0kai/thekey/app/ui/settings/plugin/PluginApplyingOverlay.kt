package com.github.klee0kai.thekey.app.ui.settings.plugin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.utils.views.collectAsStateCrossFaded

@Composable
fun PluginApplyingOverlay(
    content: @Composable () -> Unit,
) {
    val initDIScreen by LocalRouter.current.showInitDynamicFeatureScreen.collectAsStateCrossFaded(key = Unit, initial = false)

    if (initDIScreen.current) {
        PluginApplyingScreen(modifier = Modifier.alpha(initDIScreen.alpha))
        if (initDIScreen.alpha > 0.9) return
    }

    content()
}