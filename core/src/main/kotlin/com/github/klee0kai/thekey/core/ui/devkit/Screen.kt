package com.github.klee0kai.thekey.core.ui.devkit

import androidx.compose.runtime.Composable
import com.github.klee0kai.thekey.core.ui.devkit.overlay.OverlayContainer

@Composable
fun Screen(
    content: @Composable () -> Unit,
) = OverlayContainer {
    content()
}