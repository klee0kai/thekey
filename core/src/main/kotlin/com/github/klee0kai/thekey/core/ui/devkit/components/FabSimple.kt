package com.github.klee0kai.thekey.core.ui.devkit.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.theme.DefaultThemes
import com.github.klee0kai.thekey.core.utils.views.horizontal
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import org.jetbrains.annotations.VisibleForTesting

@Composable
fun FabSimple(
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    square: Boolean = false,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    val shapeRounded by animateDpAsState(if (square) 18.dp else 28.dp, label = "fab shape")
    val containerColorAnimated by animateColorAsState(containerColor, tween(), label = "")
    val contentColorAnimated by animateColorAsState(contentColor, tween(), label = "")

    FloatingActionButton(
        containerColor = containerColorAnimated,
        contentColor = contentColorAnimated,
        shape = RoundedCornerShape(shapeRounded),
        onClick = onClick,
        content = content
    )
}

@Composable
fun FabSimpleInContainer(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    square: Boolean = false,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    val safeContentPadding = WindowInsets.safeContent.asPaddingValues()

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = safeContentPadding.calculateBottomPadding() + 30.dp)
            .padding(end = safeContentPadding.horizontal(minValue = 16.dp)),
        contentAlignment = Alignment.BottomEnd
    ) {
        FabSimple(containerColor, contentColor, square, onClick, content)
    }
}

@VisibleForTesting
@Preview
@Composable
fun FabSimplePreview() = AppTheme(theme = DefaultThemes.darkTheme) {
    FabSimple {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "add"
        )
    }
}

@VisibleForTesting
@Preview
@Composable
fun FabSimpleSquarePreview() = AppTheme(theme = DefaultThemes.darkTheme) {
    FabSimple(
        square = true,
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "add"
        )
    }
}

@VisibleForTesting
@Preview(device = Devices.PHONE)
@Composable
fun FabSimpleInContainerPreview() = EdgeToEdgeTemplate {
    AppTheme(theme = DefaultThemes.darkTheme) {
        val primaryColor = MaterialTheme.colorScheme.primaryContainer
        val secondaryColor = MaterialTheme.colorScheme.secondaryContainer
        var color by remember { mutableStateOf(secondaryColor) }
        var square by remember { mutableStateOf(false) }
        FabSimpleInContainer(
            square = square,
            containerColor = color,
            onClick = {
                if (color == primaryColor) {
                    color = secondaryColor
                    square = false
                } else {
                    color = primaryColor
                    square = true
                }
            }
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "add"
            )
        }
    }
}