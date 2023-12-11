package com.github.klee0kai.thekey.app.ui.storages.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun AddCircle(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    FilledIconButton(
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
        ),
        modifier = modifier
            .size(48.dp, 48.dp),
        shape = CircleShape,
        onClick = onClick
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "add"
        )
    }

}
