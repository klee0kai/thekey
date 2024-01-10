package com.github.klee0kai.thekey.app.ui.designkit.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

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

@Composable
@Preview
fun FabSimpleInContainer(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 56.dp, end = 16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        FabSimple(onClick, content)
    }
}