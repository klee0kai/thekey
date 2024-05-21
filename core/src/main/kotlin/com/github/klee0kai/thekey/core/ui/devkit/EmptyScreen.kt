package com.github.klee0kai.thekey.core.ui.devkit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import org.jetbrains.annotations.VisibleForTesting


@Composable
fun EmptyScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    )
}

@VisibleForTesting
@Preview(device = Devices.PHONE)
@Composable
fun EmptyScreenPreview() = EdgeToEdgeTemplate {
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        )
    }
}

