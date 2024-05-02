package com.github.klee0kai.thekey.app.ui.dynamic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.github.klee0kai.thekey.app.ui.designkit.LocalRouter
import com.github.klee0kai.thekey.app.utils.views.collectAsStateCrossFaded

@Composable
fun DynamicFeatureInstallDummy(
    content: @Composable() () -> Unit,
) {
    val initDIScreen by LocalRouter.current.showInitDynamicFeatureScreen.collectAsStateCrossFaded(key = Unit, initial = false)

    if (initDIScreen.current) {
        InitDIScreen(modifier = Modifier.alpha(initDIScreen.alpha))
        if (initDIScreen.alpha > 0.9) return
    }

    content()
}