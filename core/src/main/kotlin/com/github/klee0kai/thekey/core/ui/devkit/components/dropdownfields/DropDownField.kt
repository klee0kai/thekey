@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.github.klee0kai.thekey.core.ui.devkit.components.dropdownfields

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.components.text.AppTextField
import com.github.klee0kai.thekey.core.ui.devkit.overlay.PopupMenu
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.possitions.onGlobalPositionState
import com.github.klee0kai.thekey.core.utils.possitions.rememberViewPosition
import com.github.klee0kai.thekey.core.utils.views.DebugDarkScreenPreview
import kotlinx.coroutines.flow.filterIsInstance

@Composable
fun DropDownField(
    modifier: Modifier = Modifier,
    selectedIndex: Int = 0,
    isSkeleton: Boolean = false,
    variants: List<String> = emptyList(),
    expanded: Boolean = false,
    onExpandedChange: (Boolean) -> Unit = {},
    onSelected: (Int) -> Unit = {},
    label: (@Composable () -> Unit)? = null,
) {
    val theme = LocalTheme.current
    val textFieldPosition = rememberViewPosition()
    val textFieldInteractionSource = remember { MutableInteractionSource() }

    LaunchedEffect(Unit) {
        textFieldInteractionSource.interactions
            .filterIsInstance<PressInteraction.Press>()
            .collect {
                onExpandedChange(!expanded)
            }
    }

    AppTextField(
        modifier = modifier
            .onGlobalPositionState(textFieldPosition),
        isSkeleton = isSkeleton,
        enabled = variants.isNotEmpty(),
        interactionSource = textFieldInteractionSource,
        readOnly = true,
        singleLine = true,
        value = variants.getOrNull(selectedIndex) ?: "",
        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        onValueChange = { },
        label = label,
    )

    PopupMenu(
        visible = expanded && variants.isNotEmpty(),
        positionAnchor = textFieldPosition,
        onDismissRequest = { onExpandedChange(false) }
    ) {
        SimpleSelectPopupMenu(
            modifier = Modifier
                .padding(top = 10.dp, bottom = 10.dp)
                .fillMaxWidth(),
            variants = variants,
            onSelected = { _, idx -> onSelected(idx) },
        )
    }

}

@OptIn(DebugOnly::class)
@Composable
@Preview
fun DropDownFieldPreview() = DebugDarkScreenPreview {
    var isExpanded by remember { mutableStateOf(false) }
    val variants = remember {
        buildList {
            repeat(10) { idx ->
                add("select $idx")
            }
        }
    }
    var selectedIndex by remember { mutableIntStateOf(-1) }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        DropDownField(
            expanded = isExpanded,
            variants = variants,
            selectedIndex = selectedIndex,
            onExpandedChange = { isExpanded = it },
            onSelected = {
                isExpanded = false
                selectedIndex = it
            },
            label = {
                Text(text = "select")
            }
        )
    }
}
