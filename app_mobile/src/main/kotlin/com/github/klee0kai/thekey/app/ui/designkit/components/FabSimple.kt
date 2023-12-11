package com.github.klee0kai.thekey.app.ui.designkit.components

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
@Preview
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