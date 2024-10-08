package com.github.klee0kai.thekey.app.ui.simpleboard.components.popup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.components.dropdownfields.SimpleSelectPopupMenu
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.views.DebugDarkContentPreview

@Composable
fun OpenedStoragePopupMenu(
    modifier: Modifier = Modifier,
    onLogout: (() -> Unit)? = null,
) {
    val theme = LocalTheme.current
    val colorScheme = theme.colorScheme
    val surfaceColor = LocalTheme.current.colorScheme.popupMenu.surfaceColor
    val logoutText = stringResource(id = R.string.logout)

    SimpleSelectPopupMenu(
        modifier = modifier,
        variants = buildList {
            if (onLogout != null) add(logoutText)
        },
        onSelected = { text, _ ->
            when (text) {
                logoutText -> onLogout?.invoke()
            }
        }
    )
}

@OptIn(DebugOnly::class)
@Preview
@Composable
fun OpenedStoragePopupMenuPreview() = DebugDarkContentPreview {
    Box(modifier = Modifier.background(Color.White)) {
        OpenedStoragePopupMenu(
            onLogout = {},
        )
    }
}

