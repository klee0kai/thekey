package com.github.klee0kai.thekey.app.ui.designkit.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.ui.designkit.color.SurfaceScheme

@Preview
@Composable
fun GroupCircle(
    modifier: Modifier = Modifier,
    name: String = "",
    colorScheme: SurfaceScheme = SurfaceScheme(Color.Cyan, Color.White),
    checked: Boolean = false,
    onClick: () -> Unit = {}
) {
    val checkedState by animateDpAsState(if (checked) 12.dp else 24.dp, label = "color group checked")
    val rotate by animateFloatAsState(targetValue = if (checked) 70f else 0f, label = "color group select")

    Box {
        FilledIconButton(
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = colorScheme.surfaceColor,
                contentColor = colorScheme.onSurfaceColor,
                disabledContainerColor = colorScheme.surfaceColor.copy(alpha = 0.4f),
                disabledContentColor = colorScheme.onSurfaceColor.copy(alpha = 0.4f),
            ),
            modifier = modifier
                .align(Alignment.Center)
                .rotate(rotate),
            shape = RoundedCornerShape(checkedState),
            onClick = onClick,
            content = {}
        )

        Text(
            modifier = Modifier.align(Alignment.Center),
            color = colorScheme.onSurfaceColor,
            text = name
        )
    }

}
