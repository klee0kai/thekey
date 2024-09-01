@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.github.klee0kai.thekey.core.ui.devkit.components.dropdownfields

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.color.KeyColor
import com.github.klee0kai.thekey.core.ui.devkit.components.buttons.GroupCircle
import com.github.klee0kai.thekey.core.ui.devkit.components.text.AppTextField
import com.github.klee0kai.thekey.core.ui.devkit.theme.DefaultThemes

@Composable
fun ColorGroupDropDownField(
    modifier: Modifier = Modifier,
    selectedIndex: Int = 0,
    isSkeleton :Boolean = false,
    variants: List<ColorGroup> = emptyList(),
    expanded: Boolean = false,
    onExpandedChange: (Boolean) -> Unit = {},
    onSelected: (Int) -> Unit = {},
    label: (@Composable () -> Unit)? = null,
) {
    val theme = LocalTheme.current

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = modifier,
    ) {
        AppTextField(
            modifier = Modifier
                .menuAnchor(),
            readOnly = true,
            singleLine = true,
            isSkeleton = isSkeleton,
            value = variants.getOrNull(selectedIndex)?.name ?: "",
            leadingIcon = {
                GroupCircle(
                    modifier = Modifier
                        .padding(4.dp),
                    colorScheme = theme.colorScheme.surfaceSchemas.surfaceScheme(
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
                            colorScheme = theme.colorScheme.surfaceSchemas.surfaceScheme(item.keyColor),
                            checked = index == selectedIndex,
                            onClick = { onSelected(index) },
                        )
                    },
                    onClick = { onSelected(index) }
                )
            }
        }
    }
}

@Composable
@Preview
fun ColorGroupDropDownFieldPreview() = AppTheme(theme = DefaultThemes.darkTheme) {
    ColorGroupDropDownField()
}
