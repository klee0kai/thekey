package com.github.klee0kai.thekey.app.ui.designkit.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.ui.designkit.AppTheme
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
            .padding(vertical = 12.dp)
            .padding(start = 16.dp, end = 16.dp)
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

