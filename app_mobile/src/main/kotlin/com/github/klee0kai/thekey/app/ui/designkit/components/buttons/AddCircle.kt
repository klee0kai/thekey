@file:OptIn(ExperimentalFoundationApi::class)

package com.github.klee0kai.thekey.app.ui.designkit.components.buttons

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.ui.designkit.AppTheme

@Composable
fun AddCircle(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    overlayContent: @Composable () -> Unit = {},
) {
    Box(
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .minimumInteractiveComponentSize()
                .size(40.dp)
                .background(color = MaterialTheme.colorScheme.surface, shape = CircleShape)
                .clip(CircleShape)
                .run {
                    when {
                        onClick == null && onLongClick == null -> this
                        else -> combinedClickable(
                            onLongClick = onLongClick,
                            onClick = { onClick?.invoke() }
                        )
                    }
                },
        )

        Icon(
            modifier = Modifier.align(Alignment.Center),
            imageVector = Icons.Filled.Add,
            contentDescription = "add"
        )

        overlayContent()
    }
}

@Preview
@Composable
private fun AddCirclePreview() {
    AppTheme {
        AddCircle()
    }
}
