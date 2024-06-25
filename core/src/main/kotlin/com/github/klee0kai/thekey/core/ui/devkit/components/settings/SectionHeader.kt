package com.github.klee0kai.thekey.core.ui.devkit.components.settings

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.theme.DefaultThemes
import com.github.klee0kai.thekey.core.utils.views.minInsets
import com.github.klee0kai.thekey.core.utils.views.truncate
import org.jetbrains.annotations.VisibleForTesting

@Composable
fun SectionHeader(
    modifier: Modifier = Modifier,
    text: String = "",
) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineLarge,
        modifier = modifier
            .windowInsetsPadding(
                WindowInsets.safeContent
                    .minInsets(16.dp)
                    .truncate(top = true, bottom = true)
            )
            .padding(top = 16.dp)
            .alpha(0.4f)
    )
}


@Preview
@VisibleForTesting
@Composable
fun SectionHeaderPreview() = AppTheme(theme = DefaultThemes.darkTheme) {
    SectionHeader(text = "Some Section")
}

