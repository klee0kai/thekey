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
fun GroupPopupMenu(
    modifier: Modifier = Modifier,
    onEdit: () -> Unit = {},
) {
    val theme = LocalTheme.current
    val editText = stringResource(id = R.string.edit)

    SimpleSelectPopupMenu(
        modifier = modifier,
        variants = listOf(editText),
        onSelected = { text, _ ->
            when (text) {
                editText -> onEdit()
            }
        }
    )
}


@OptIn(DebugOnly::class)
@Preview
@Composable
fun GroupPopupMenuPreview() = DebugDarkContentPreview {
    Box(modifier = Modifier.background(Color.White)) {
        GroupPopupMenu()
    }
}