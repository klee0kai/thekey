package com.github.klee0kai.thekey.app.ui.storages.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.ui.designkit.AppTheme

@Composable
fun ColumnScope.GroupDropDownMenuContent(
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
) {
    DropdownMenuItem(
        modifier = Modifier.align(Alignment.End),
        text = { Text(text = stringResource(id = R.string.edit)) },
        onClick = onEdit,
    )

    DropdownMenuItem(
        modifier = Modifier.align(Alignment.End),
        text = { Text(text = stringResource(id = R.string.remove)) },
        onClick = onDelete,
    )
}

@Preview
@Composable
private fun NoteDropDownMenuWithGroupsPreview() = AppTheme {
    Column {
        GroupDropDownMenuContent()
    }
}

