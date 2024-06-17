package com.github.klee0kai.thekey.core.ui.devkit.components.appbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.theme.DefaultThemes
import org.jetbrains.annotations.VisibleForTesting

@Composable
fun DoneIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    IconButton(
        modifier = modifier,
        onClick = onClick,
    ) {
        Icon(
            imageVector = Icons.Filled.Done,
            contentDescription = stringResource(id = R.string.done),
            tint = MaterialTheme.colorScheme.onBackground
        )
    }
}

@VisibleForTesting
@Preview
@Composable
fun DoneButtonPreview() = AppTheme(theme = DefaultThemes.darkTheme) {
    AppBarStates(
        actions = {
            DoneIconButton()
        }
    )
}