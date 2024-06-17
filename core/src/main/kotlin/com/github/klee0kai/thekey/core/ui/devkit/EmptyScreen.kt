package com.github.klee0kai.thekey.core.ui.devkit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.github.klee0kai.thekey.core.ui.devkit.theme.DefaultThemes
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import org.jetbrains.annotations.VisibleForTesting


@Composable
fun EmptyScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
    )
}

@VisibleForTesting
@Preview(device = Devices.PHONE)
@Composable
fun EmptyScreenPreview() = EdgeToEdgeTemplate {
    AppTheme(theme = DefaultThemes.darkTheme) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

