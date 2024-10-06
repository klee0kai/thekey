package com.github.klee0kai.thekey.app.ui.login.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.hardResetToPreview
import com.github.klee0kai.thekey.core.domain.model.ColoredStorage
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.views.DebugDarkContentPreview
import com.github.klee0kai.thekey.core.utils.views.ifProduction
import com.github.klee0kai.thekey.core.utils.views.toAnnotationString

@Composable
fun SelectedStorageElement(
    modifier: Modifier = Modifier,
    coloredStorage: ColoredStorage = ColoredStorage(),
) {
    val theme = LocalTheme.current
    val pathInputHelper = remember { DI.pathInputHelper() }

    val shortStoragePath = with(pathInputHelper) {
        coloredStorage.path
            .shortPath()
            .toAnnotationString()
            .coloredPath(accentColor = theme.colorScheme.androidColorScheme.primary)
            .coloredFileExt(extensionColor = theme.colorScheme.textColors.hintTextColor)
    }

    Column(
        modifier = modifier,
    ) {

        if (coloredStorage.name.isNotBlank()) {
            Text(
                text = coloredStorage.name,
                style = theme.typeScheme.bodySmall,
                modifier = Modifier
                    .align(Alignment.Start)
                    .ifProduction { animateContentSize() },
            )
        }

        Text(
            text = shortStoragePath,
            style = theme.typeScheme.bodySmall,
            modifier = Modifier
                .align(Alignment.End)
                .ifProduction { animateContentSize() }
        )
    }
}

@OptIn(DebugOnly::class)
@Preview
@Composable
fun SelectedStoragePreview() {
    DI.hardResetToPreview()

    DebugDarkContentPreview {
        SelectedStorageElement(
            modifier = Modifier.fillMaxWidth(),
            coloredStorage = ColoredStorage(
                path = "/appdata/some_path",
                name = "editModeStorage"
            )
        )
    }
}