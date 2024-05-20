package com.github.klee0kai.thekey.app.ui.designkit.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.ui.designkit.AppTheme
import com.github.klee0kai.thekey.core.utils.views.minInsets
import com.github.klee0kai.thekey.core.utils.views.truncate
import org.jetbrains.annotations.VisibleForTesting

@Composable
fun SwitchPreference(
    modifier: Modifier = Modifier,
    text: String = "",
    checked: Boolean = false,
    onCheckedChange: (Boolean) -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = { onCheckedChange(!checked) })
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = text, modifier = Modifier
                .align(Alignment.CenterVertically)
                .windowInsetsPadding(
                    WindowInsets.safeContent
                        .minInsets(16.dp)
                        .truncate(top = true, bottom = true)
                )
        )
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            modifier = Modifier
                .windowInsetsPadding(
                    WindowInsets.safeContent
                        .minInsets(16.dp)
                        .truncate(top = true, bottom = true)
                ),
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}


@Preview
@VisibleForTesting
@Composable
fun SwitchPreferencePreview() = AppTheme {
    SwitchPreference(
        text = "Some Switch",
    )
}

