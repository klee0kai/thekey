package com.github.klee0kai.thekey.dynamic.findstorage.ui.editstorage.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.components.text.AppTextField
import com.github.klee0kai.thekey.core.ui.devkit.overlay.PopupMenu
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.file.appendSuffix
import com.github.klee0kai.thekey.core.utils.file.removeFileExtension
import com.github.klee0kai.thekey.core.utils.possitions.onGlobalPositionState
import com.github.klee0kai.thekey.core.utils.possitions.pxToDp
import com.github.klee0kai.thekey.core.utils.possitions.rememberViewPosition
import com.github.klee0kai.thekey.core.utils.views.DebugDarkScreenPreview
import com.github.klee0kai.thekey.core.utils.views.thenIf
import com.github.klee0kai.thekey.core.utils.views.toTextFieldValue
import com.github.klee0kai.thekey.core.utils.views.toTransformationText
import com.github.klee0kai.thekey.core.utils.views.withTKeyExtension
import com.github.klee0kai.thekey.dynamic.findstorage.di.FSDI
import com.github.klee0kai.thekey.dynamic.findstorage.di.hardResetToPreview
import com.github.klee0kai.thekey.dynamic.findstorage.domain.model.FileItem
import kotlinx.coroutines.flow.filterIsInstance
import java.io.File

@Composable
fun PathTextField(
    modifier: Modifier = Modifier,
    value: TextFieldValue = TextFieldValue(),
    label: String = "",
    providerHint: String = "",
    variants: List<FileItem> = emptyList(),
    isSkeleton: Boolean = false,
    expanded: Boolean = false,
    onExpandedChange: (Boolean) -> Unit = {},
    onValueChange: (TextFieldValue) -> Unit = {},

    ) {
    val theme = LocalTheme.current
    val textFieldPosition = rememberViewPosition()
    val textFieldInteractionSource = remember { MutableInteractionSource() }
    val pathInputHelper = remember { FSDI.pathInputHelper() }
    val safeContentPaddings = WindowInsets.safeContent.asPaddingValues()

    LaunchedEffect(Unit) {
        textFieldInteractionSource.interactions
            .filterIsInstance<PressInteraction.Press>()
            .collect { onExpandedChange(!expanded) }
    }

    AppTextField(
        modifier = modifier
            .onGlobalPositionState(textFieldPosition),
        isSkeleton = isSkeleton,
        interactionSource = textFieldInteractionSource,
        visualTransformation = { input ->
            with(pathInputHelper) {
                input.coloredPath(theme.colorScheme.androidColorScheme.primary)
                    .toTransformationText()
                    .withTKeyExtension(theme.colorScheme.hintTextColor)
            }
        },
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
    )

    PopupMenu(
        visible = expanded,
        positionAnchor = textFieldPosition,
        onDismissRequest = { onExpandedChange(false) }
    ) {
        val surface = theme.colorScheme.popupMenu.surfaceColor
        val container = rememberViewPosition()

        LazyColumn(
            modifier = Modifier
                .padding(top = 10.dp, bottom = 10.dp)
                .fillMaxWidth()
                .onGlobalPositionState(container)
                .heightIn(0.dp, 600.dp)
                .background(
                    color = surface,
                    shape = RoundedCornerShape(16.dp)
                ),
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Text(
                    text = providerHint,
                    modifier = Modifier.padding(
                        horizontal = 8.dp,
                        vertical = 8.dp,
                    ),
                    style = theme.typeScheme.body,
                )
            }

            variants.forEachIndexed { _, file ->
                item {
                    FileNameElement(
                        modifier = Modifier
                            .clickable {
                                onValueChange(
                                    file.userPath
                                        .thenIf(!file.isFolder) { removeFileExtension() }
                                        .thenIf(file.isFolder) { appendSuffix(File.separator) }
                                        .toTextFieldValue()
                                )
                            }
                            .defaultMinSize(
                                minWidth = container.value?.size?.width?.pxToDp() ?: 200.dp
                            ),
                        fileItem = file,
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

        }
    }

}

@OptIn(DebugOnly::class)
@Composable
@Preview
fun PathTextFieldPreview() {
    FSDI.hardResetToPreview()

    DebugDarkScreenPreview {
        var isExpanded by remember { mutableStateOf(true) }
        val variants = remember {
            buildList {
                add(FileItem("/app/thekey/data", isFolder = true))
                add(FileItem("/storage/emulated/0", isFolder = true))
            }
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(100.dp))
            PathTextField(
                expanded = isExpanded,
                variants = variants,
                onExpandedChange = { isExpanded = it },
                label = "storage path",
                providerHint = "Available files and folders",
            )
        }
    }
}
