package com.github.klee0kai.thekey.core.ui.devkit.components.dropdownfields

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.possitions.onGlobalPositionState
import com.github.klee0kai.thekey.core.utils.possitions.pxToDp
import com.github.klee0kai.thekey.core.utils.possitions.rememberViewPosition
import com.github.klee0kai.thekey.core.utils.views.DebugDarkContentPreview

@Composable
fun SimpleSelectPopupMenu(
    modifier: Modifier = Modifier,
    surface: Color = LocalTheme.current.colorScheme.popupMenu.surfaceColor,
    variants: List<String> = emptyList(),
    onSelected: (variant: String, index: Int) -> Unit = { _, _ -> },
) {

    if (variants.isEmpty()) return
    val container = rememberViewPosition()

    LazyColumn(
        modifier = modifier
            .onGlobalPositionState(container)
            .heightIn(0.dp, 200.dp)
            .background(color = surface, shape = RoundedCornerShape(16.dp)),
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
        variants.forEachIndexed { inx, text ->
            item {
                Text(
                    text = text,
                    modifier = Modifier
                        .clickable { onSelected.invoke(text, inx) }
                        .defaultMinSize(minWidth = container.value?.size?.width?.pxToDp() ?: 200.dp)
                        .padding(all = 12.dp)
                        .padding(start = 8.dp, end = 28.dp)

                )
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
fun SimpleSelectPopupMenuPreview() = DebugDarkContentPreview {
    SimpleSelectPopupMenu(
        variants = listOf(
            LoremIpsum(2).values.joinToString { it },
            LoremIpsum(4).values.joinToString { it }
        )
    )
}