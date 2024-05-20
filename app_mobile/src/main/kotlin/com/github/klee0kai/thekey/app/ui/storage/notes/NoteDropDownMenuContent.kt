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
import com.github.klee0kai.thekey.app.ui.designkit.components.buttons.GroupCircle
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalColorScheme
import com.github.klee0kai.thekey.core.ui.devkit.color.KeyColor
import com.github.klee0kai.thekey.core.utils.common.Dummy
import org.jetbrains.annotations.VisibleForTesting

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
                modifier = Modifier
                    .padding(
                        start = 8.dp,
                        top = 8.dp,
                        bottom = 8.dp
                    ),
                name = group.name,
                colorScheme = colorScheme.surfaceScheme(group.keyColor),
                checked = group.id == selectedGroupId,
                onClick = { onColorGroupSelected.invoke(group) },
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


@VisibleForTesting
@Preview
@Composable
fun NoteDropDownMenuWithGroupsPreview() = AppTheme {
    Column {
        NoteDropDownMenuContent(
            selectedGroupId = 1L,
            colorGroups = listOf(
                ColorGroup(Dummy.dummyId, name = "AN", keyColor = KeyColor.VIOLET),
                ColorGroup(Dummy.dummyId, name = "QW", keyColor = KeyColor.TURQUOISE),
                ColorGroup(Dummy.dummyId, name = "Q", keyColor = KeyColor.ORANGE),
                ColorGroup(Dummy.dummyId, name = "W", keyColor = KeyColor.VIOLET),
                ColorGroup(Dummy.dummyId, name = "AA", keyColor = KeyColor.PINK),
                ColorGroup(Dummy.dummyId, name = "P", keyColor = KeyColor.NOCOLOR),
                ColorGroup(Dummy.dummyId, name = "P", keyColor = KeyColor.NOCOLOR),
            )
        )
    }
}


@VisibleForTesting
@Preview
@Composable
fun NoteDropDownMenuNoPreview() = AppTheme {
    Column {
        NoteDropDownMenuContent()
    }
}
