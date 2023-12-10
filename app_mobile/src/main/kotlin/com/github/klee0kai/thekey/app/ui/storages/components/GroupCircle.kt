package com.github.klee0kai.thekey.app.ui.storages.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.ui.designkit.color.SurfaceScheme

@Preview
@Composable
fun GroupCircle(
    modifier: Modifier = Modifier,
    name: String = "A",
    colorScheme: SurfaceScheme = SurfaceScheme(Color.Cyan, Color.White),
    onClick: () -> Unit = {}
) {
    FilledIconButton(
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = colorScheme.surfaceColor,
            contentColor = colorScheme.onSurfaceColor,
            disabledContainerColor = colorScheme.surfaceColor.copy(alpha = 0.4f),
            disabledContentColor = colorScheme.onSurfaceColor.copy(alpha = 0.4f),
        ),
        modifier = modifier
            .size(48.dp, 48.dp),
        shape = CircleShape,
        onClick = onClick
    ) {
        Text(text = name)
    }

}
