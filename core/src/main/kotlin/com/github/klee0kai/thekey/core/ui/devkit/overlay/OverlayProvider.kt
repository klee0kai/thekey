package com.github.klee0kai.thekey.core.ui.devkit.overlay

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

val LocalOverlayProvider = compositionLocalOf<OverlayProvider> { error("overlay no available") }

fun interface OverlayProvider {

    @Composable
    fun Overlay(block: @Composable () -> Unit)

}

@Composable
fun OverlayContainer(
    content: @Composable () -> Unit,
) {
    var overlay by remember { mutableStateOf<(@Composable () -> Unit)?>(null) }

    CompositionLocalProvider(
        LocalOverlayProvider provides OverlayProvider { block -> overlay = block },
    ) {
        content()

        overlay?.invoke()
    }
}