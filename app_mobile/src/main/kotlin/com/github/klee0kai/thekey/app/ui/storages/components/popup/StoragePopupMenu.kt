package com.github.klee0kai.thekey.app.ui.storages.components.popup

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
fun StoragePopupMenu(
    modifier: Modifier = Modifier,
    onEdit: (() -> Unit)? = null,
    onExport: (() -> Unit)? = null,
) {
    val theme = LocalTheme.current
    val exportText = stringResource(id = R.string.export)
    val editText = stringResource(id = R.string.edit)

    SimpleSelectPopupMenu(
        modifier = modifier,
        variants = buildList {
            if (onEdit != null) add(editText)
            if (onExport != null) add(exportText)
        },
        onSelected = { text, _ ->
            when (text) {
                editText -> onEdit?.invoke()
                exportText -> onExport?.invoke()
            }
        }
    )

}

@OptIn(DebugOnly::class)
@Preview
@Composable
private fun StoragePopupMenuPreview() = DebugDarkContentPreview {
    Box(modifier = Modifier.background(Color.White)) {
        StoragePopupMenu(
            onExport = {},
            onEdit = {},
        )
    }
}

