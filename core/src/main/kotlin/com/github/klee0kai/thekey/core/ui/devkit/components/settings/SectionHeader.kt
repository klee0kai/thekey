package com.github.klee0kai.thekey.core.ui.devkit.components.settings

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.theme.DefaultThemes
import com.github.klee0kai.thekey.core.utils.views.horizontal
import org.jetbrains.annotations.VisibleForTesting

@Composable
fun SectionHeader(
    modifier: Modifier = Modifier,
    text: String = "",
) {
    val theme = LocalTheme.current
    val safeContentPadding = WindowInsets.safeContent.asPaddingValues()

    Text(
        text = text,
        style = theme.typeScheme.header,
        color = theme.colorScheme.textColors.hintTextColor,
        modifier = modifier
            .padding(top = 16.dp)
            .padding(horizontal = safeContentPadding.horizontal(minValue = 16.dp)),
    )
}


@Preview
@VisibleForTesting
@Composable
fun SectionHeaderPreview() = AppTheme(theme = DefaultThemes.darkTheme) {
    SectionHeader(text = "Some Section")
}

