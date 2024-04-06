@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.klee0kai.thekey.app.ui.designkit.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.model.ColorGroup
import com.github.klee0kai.thekey.app.ui.designkit.color.KeyColor
import com.github.klee0kai.thekey.app.ui.navigation.LocalColorScheme
import com.github.klee0kai.thekey.app.ui.storages.components.GroupCircle

@Preview
@Composable
fun ColorGroupDropDownField(
    modifier: Modifier = Modifier,
    selectedIndex: Int = 0,
    variants: List<ColorGroup> = emptyList(),
    expanded: Boolean = false,
    onExpandedChange: (Boolean) -> Unit = {},
    onSelected: (Int) -> Unit = {},
    label: (@Composable () -> Unit)? = null,
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = modifier,
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor(),
            readOnly = true,
            singleLine = true,
            value = variants.getOrNull(selectedIndex)?.name ?: "",
            leadingIcon = {
                GroupCircle(
                    modifier = Modifier
                        .padding(4.dp),
                    colorScheme = LocalColorScheme.current.surfaceScheme(
                        variants.getOrNull(selectedIndex)?.keyColor ?: KeyColor.NOCOLOR
                    ),
                )
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            onValueChange = { },
            label = label
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            variants.forEachIndexed { index, item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = item.name,
                            fontWeight = if (index == selectedIndex) FontWeight.Bold else null
                        )
                    },
                    leadingIcon = {
                        GroupCircle(
                            modifier = Modifier
                                .padding(4.dp),
                            colorScheme = LocalColorScheme.current.surfaceScheme(item.keyColor),
                            checked = index == selectedIndex,
                            onClick = { onSelected(index) }
                        )
                    },
                    onClick = { onSelected(index) }
                )
            }
        }

    }
}
