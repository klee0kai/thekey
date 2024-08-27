package com.github.klee0kai.thekey.core.ui.devkit.components.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.views.DebugDarkContentPreview
import org.jetbrains.annotations.VisibleForTesting

@Composable
fun SwitchPreference(
    modifier: Modifier = Modifier,
    text: String = "",
    checked: Boolean = false,
) {

    Row(
        modifier = modifier,
    ) {
        Text(
            text = text,
            modifier = Modifier
                .align(Alignment.CenterVertically)
        )
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            modifier = Modifier,
            checked = checked,
            onCheckedChange = null
        )
    }
}


@OptIn(DebugOnly::class)
@Preview
@VisibleForTesting
@Composable
fun SwitchPreferencePreview() = DebugDarkContentPreview {
    var checked by remember { mutableStateOf(false) }
    SwitchPreference(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { checked = !checked })
            .padding(vertical = 12.dp)
            .padding(horizontal = 16.dp),
        text = "Some Switch",
        checked = checked,
    )
}

