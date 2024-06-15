package com.github.klee0kai.thekey.app.ui.login.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.klee0kai.thekey.core.domain.model.ColoredStorage
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme

@Composable
fun SelectedStorage(
    modifier: Modifier = Modifier,
    coloredStorage: ColoredStorage = ColoredStorage(),
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            text = coloredStorage.name,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .align(Alignment.Start)
        )

        Text(
            text = coloredStorage.path,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .align(Alignment.End)
        )
    }
}

@Preview
@Composable
fun SelectedStoragePreview() = AppTheme {
    SelectedStorage(
        modifier = Modifier.fillMaxWidth(),
        coloredStorage = ColoredStorage(path = "/app_folder/some_path", name = "editModeStorage")
    )
}