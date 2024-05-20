package com.github.klee0kai.thekey.app.ui.designkit.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.utils.views.minInsets
import com.github.klee0kai.thekey.core.utils.views.truncate
import org.jetbrains.annotations.VisibleForTesting

@Composable
fun Preference(
    modifier: Modifier = Modifier,
    text: String = "",
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .windowInsetsPadding(
                WindowInsets.safeContent
                    .minInsets(16.dp)
                    .truncate(top = true, bottom = true)
            )
            .padding(vertical = 12.dp)
            .fillMaxWidth()
    ) {
        Text(text = text)
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
            contentDescription = null
        )
    }
}


@Preview
@VisibleForTesting
@Composable
fun PreferencePreview() = AppTheme {
    Preference(text = "Some Preference")
}

