package com.github.klee0kai.thekey.core.ui.devkit.components.dropdownfields

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.color.KeyColor
import com.github.klee0kai.thekey.core.ui.devkit.components.buttons.GroupCircle
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.common.Dummy
import com.github.klee0kai.thekey.core.utils.views.DebugDarkContentPreview

@Composable
fun ColorGroupSelectPopupMenu(
    modifier: Modifier = Modifier,
    surface: Color = LocalTheme.current.colorScheme.popupMenu.surfaceColor,
    variants: List<ColorGroup> = emptyList(),
    selectedIndex: Int = -1,
    onSelected: (group: ColorGroup, index: Int) -> Unit = { _, _ -> },
) {
    val theme = LocalTheme.current
    val scrollState = rememberScrollState()

    if (variants.isEmpty()) return

    LazyColumn(
        modifier = modifier
            .verticalScroll(scrollState)
            .heightIn(0.dp, 330.dp)
            .background(
                color = surface,
                shape = RoundedCornerShape(16.dp)
            ),
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
        variants.forEachIndexed { index, group ->
            item(group.id) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelected.invoke(group, index) }
                ) {
                    Row {
                        GroupCircle(
                            checked = index == selectedIndex,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            colorScheme = theme.colorScheme.surfaceSchemas.surfaceScheme(group.keyColor),
                        )

                        Text(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            text = group.name.takeIf { it.isNotBlank() } ?: "no name"
                        )
                    }
                }

            }
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


@Preview
@DebugOnly
@Composable
fun ColorGroupSelectPopupMenuPreview() = DebugDarkContentPreview {
    ColorGroupSelectPopupMenu(
        selectedIndex = 1,
        variants = listOf(
            ColorGroup(Dummy.dummyId, "A", KeyColor.PINK),
            ColorGroup(Dummy.dummyId, "B", KeyColor.ORANGE),
            ColorGroup(Dummy.dummyId, "", KeyColor.ORANGE),

            )
    )
}