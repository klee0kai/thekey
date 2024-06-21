package com.github.klee0kai.thekey.core.ui.devkit.overlay

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

val LocalOverlayProvider = compositionLocalOf<OverlayProvider> { error("overlay no available") }

interface OverlayProvider {

    @Composable
    fun Overlay(key: Any, block: @Composable () -> Unit)

    fun clean(key: Any)

}

@Composable
fun OverlayContainer(
    content: @Composable () -> Unit,
) {
    var overlayKey by remember { mutableStateOf<Any?>(null) }
    var overlay by remember { mutableStateOf<(@Composable () -> Unit)?>(null) }

    val provider = object : OverlayProvider {
        @Composable
        override fun Overlay(key: Any, block: @Composable () -> Unit) {
            if (key != overlayKey) {
                overlay = block
                overlayKey = key
            }
        }

        override fun clean(key: Any) {
            if (overlayKey == key) {
                overlayKey = null
                overlay = null
            }
        }
    }

    CompositionLocalProvider(
        LocalOverlayProvider provides provider,
    ) {
        content()

        overlay?.invoke()
    }
}