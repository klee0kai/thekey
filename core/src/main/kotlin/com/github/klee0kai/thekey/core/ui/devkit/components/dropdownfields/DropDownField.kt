@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.github.klee0kai.thekey.core.ui.devkit.components.dropdownfields

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.components.text.AppTextField
import org.jetbrains.annotations.VisibleForTesting

@Composable
fun DropDownField(
    modifier: Modifier = Modifier,
    selectedIndex: Int = 0,
    variants: List<String> = emptyList(),
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
        AppTextField(
            modifier = Modifier
                .menuAnchor(),
            readOnly = true,
            singleLine = true,
            value = variants.getOrNull(selectedIndex) ?: "",
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
                            text = item,
                            fontWeight = if (index == selectedIndex) FontWeight.Bold else null
                        )
                    },
                    onClick = { onSelected(index) }
                )
            }
        }

    }
}

@VisibleForTesting
@Preview
@Composable
fun DropDownFieldEmptyPreview() = AppTheme {
    DropDownField(
        selectedIndex = 0,
        variants = emptyList(),
        label = { Text(stringResource(R.string.type)) }
    )
}

@VisibleForTesting
@Preview
@Composable
fun DropDownFieldSelectedPreview() = AppTheme {
    DropDownField(
        selectedIndex = 1,
        variants = listOf(
            "Type1",
            "Type2"
        ),
        label = { Text(stringResource(R.string.type)) }
    )
}

@VisibleForTesting
@Preview
@Composable
fun DropDownFieldExpandedPreview() = AppTheme {
    DropDownField(
        selectedIndex = 1,
        expanded = true,
        variants = listOf(
            "Type1",
            "Type2"
        ),
        label = { Text(stringResource(R.string.type)) }
    )
}
