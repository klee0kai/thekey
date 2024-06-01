package com.github.klee0kai.thekey.core.ui.devkit.components.appbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalColorScheme
import org.jetbrains.annotations.VisibleForTesting

@Composable
fun DeleteIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    IconButton(
        modifier = modifier,
        onClick = onClick,
    ) {
        Icon(
            imageVector = Icons.Filled.Delete,
            contentDescription = stringResource(id = R.string.delete),
            tint = LocalColorScheme.current.deleteColor,
        )
    }
}

@VisibleForTesting
@Preview
@Composable
fun DeleteButtonPreview() = AppTheme {
    AppBarStates(
        actions = {
            DeleteIconButton()
        }
    )
}