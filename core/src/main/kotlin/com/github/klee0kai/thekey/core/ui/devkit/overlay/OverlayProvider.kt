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
    fun Overlay(key: Any, block: @Composable () -> Unit)

}

@Composable
fun OverlayContainer(
    content: @Composable () -> Unit,
) {
    var overlayKey by remember { mutableStateOf<Any?>(null) }
    var overlay by remember { mutableStateOf<(@Composable () -> Unit)?>(null) }
//    LaunchedEffect(overlay.hashCode()) {
//        delay(100)
//        overlay = null
//    }

    CompositionLocalProvider(
        LocalOverlayProvider provides OverlayProvider { key, block ->
            if (key != overlayKey) {
                overlay = block
                overlayKey = key
            }
        },
    ) {
        content()

        overlay?.invoke()
    }
}