package com.github.klee0kai.thekey.app.ui.hist.components.popup

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
fun HistPasswPopup(
    modifier: Modifier = Modifier,
    onSave: (() -> Unit)? = null,
    onCopy: (() -> Unit)? = null,
    onRemove: (() -> Unit)? = null,
) {
    val theme = LocalTheme.current
    val colorScheme = theme.colorScheme
    val surfaceColor = LocalTheme.current.colorScheme.popupMenu.surfaceColor
    val saveText = stringResource(id = R.string.save)
    val copyText = stringResource(id = R.string.copy)
    val removeText = stringResource(id = R.string.remove)

    SimpleSelectPopupMenu(
        modifier = modifier,
        variants = buildList {
            if (onSave != null) add(saveText)
            if (onCopy != null) add(copyText)
            if (onRemove != null) add(removeText)
        },
        onSelected = { text, _ ->
            when (text) {
                saveText -> onSave?.invoke()
                copyText -> onCopy?.invoke()
                removeText -> onRemove?.invoke()
            }
        }
    )
}

@OptIn(DebugOnly::class)
@Preview
@Composable
private fun HistPasswPopupPreview() = DebugDarkContentPreview {
    Box(modifier = Modifier.background(Color.White)) {
        HistPasswPopup(
            onSave = {},
            onCopy = {},
            onRemove = {},
        )
    }
}

