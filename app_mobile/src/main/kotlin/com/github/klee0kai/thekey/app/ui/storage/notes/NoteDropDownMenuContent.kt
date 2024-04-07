package com.github.klee0kai.thekey.app.ui.storage.notes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.domain.model.ColorGroup
import com.github.klee0kai.thekey.app.ui.designkit.AppTheme
import com.github.klee0kai.thekey.app.ui.designkit.LocalColorScheme
import com.github.klee0kai.thekey.app.ui.designkit.color.KeyColor
import com.github.klee0kai.thekey.app.ui.designkit.components.GroupCircle
import com.github.klee0kai.thekey.app.utils.common.DummyId

@Composable
fun ColumnScope.NoteDropDownMenuContent(
    modifier: Modifier = Modifier,
    colorGroups: List<ColorGroup> = emptyList(),
    selectedGroupId: Long? = null,
    onColorGroupSelected: (ColorGroup) -> Unit = {},
    onEdit: () -> Unit = {},
) {
    val colorScheme = LocalColorScheme.current

    Row(
        modifier = modifier
    ) {
        colorGroups.take(5).forEachIndexed { index, group ->
            GroupCircle(
                name = group.name,
                colorScheme = colorScheme.surfaceScheme(group.keyColor),
                checked = group.id == selectedGroupId,
                modifier = Modifier
                    .padding(
                        start = 8.dp,
                        top = 8.dp,
                        bottom = 8.dp
                    ),
                onClick = { onColorGroupSelected.invoke(group) }
            )
        }

        Spacer(modifier = Modifier.width(8.dp))
    }

    DropdownMenuItem(
        modifier = Modifier.align(Alignment.End),
        text = { Text(text = stringResource(id = R.string.edit)) },
        onClick = onEdit
    )
}


@Preview
@Composable
fun NoteDropDownMenuWithGroupsPreview() {
    AppTheme {
        Column {
            NoteDropDownMenuContent(
                selectedGroupId = 1L,
                colorGroups = listOf(
                    ColorGroup(DummyId.dummyId, name = "AN", keyColor = KeyColor.VIOLET),
                    ColorGroup(DummyId.dummyId, name = "QW", keyColor = KeyColor.TURQUOISE),
                    ColorGroup(DummyId.dummyId, name = "Q", keyColor = KeyColor.ORANGE),
                    ColorGroup(DummyId.dummyId, name = "W", keyColor = KeyColor.VIOLET),
                    ColorGroup(DummyId.dummyId, name = "AA", keyColor = KeyColor.PINK),
                    ColorGroup(DummyId.dummyId, name = "P", keyColor = KeyColor.NOCOLOR),
                    ColorGroup(DummyId.dummyId, name = "P", keyColor = KeyColor.NOCOLOR),
                )
            )
        }
    }
}


@Preview
@Composable
fun NoteDropDownMenuNoPreview() {
    AppTheme {
        Column {
            NoteDropDownMenuContent()
        }
    }
}