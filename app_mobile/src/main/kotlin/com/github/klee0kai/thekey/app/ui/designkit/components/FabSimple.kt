package com.github.klee0kai.thekey.app.ui.designkit.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.ui.designkit.AppTheme

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
            .padding(bottom = 56.dp, end = 16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        FabSimple(onClick, content)
    }
}


@Preview
@Composable
private fun FabSimplePreview() {
    AppTheme {
        FabSimple {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "add"
            )
        }
    }
}

@Preview
@Composable
private fun FabSimpleInContainerPreview() {
    AppTheme {
        FabSimpleInContainer {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "add"
            )
        }
    }
}