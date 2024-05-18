package com.github.klee0kai.thekey.app.ui.designkit.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.ui.designkit.AppTheme
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import org.jetbrains.annotations.VisibleForTesting

@Composable
fun FabSimple(
    onClick: () -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    FloatingActionButton(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        shape = FloatingActionButtonDefaults.largeShape,
        onClick = onClick,
        content = content
    )
}

@Composable
fun FabSimpleInContainer(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeContent)
            .padding(bottom = 56.dp, end = 16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        FabSimple(onClick, content)
    }
}

@VisibleForTesting
@Preview()
@Composable
fun FabSimplePreview() = AppTheme {
    FabSimple {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "add"
        )
    }
}

@VisibleForTesting
@Preview(device = Devices.PHONE)
@Composable
fun FabSimpleInContainerPreview() = EdgeToEdgeTemplate {
    AppTheme {
        FabSimpleInContainer {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "add"
            )
        }
    }
}