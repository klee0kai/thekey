@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.klee0kai.thekey.app.ui.designkit.components.dropdownfields

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.ui.designkit.AppTheme

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
        OutlinedTextField(
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

@Preview
@Composable
private fun DropDownFieldEmptyPreview() = AppTheme {
    DropDownField(
        selectedIndex = 0,
        variants = emptyList(),
        label = { Text(stringResource(R.string.type)) }
    )
}

@Preview
@Composable
private fun DropDownFieldSelectedPreview() = AppTheme {
    DropDownField(
        selectedIndex = 1,
        variants = listOf(
            "Type1",
            "Type2"
        ),
        label = { Text(stringResource(R.string.type)) }
    )
}

@Preview
@Composable
private fun DropDownFieldExpandedPreview() = AppTheme {
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
