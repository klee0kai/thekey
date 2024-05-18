package com.github.klee0kai.thekey.app.ui.designkit.settings

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
import com.github.klee0kai.thekey.app.ui.designkit.AppTheme
import com.github.klee0kai.thekey.app.utils.views.minInsets
import com.github.klee0kai.thekey.app.utils.views.truncate
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
fun SectionHeaderPreview() = AppTheme {
    SectionHeader(text = "Some Section")
}

