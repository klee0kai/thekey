package com.github.klee0kai.thekey.core.ui.devkit.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable

@Composable
fun BackMenuIcon(
    isMenu: Boolean = false,
) {
    Icon(
        imageVector = if (isMenu) {
            Icons.Filled.Menu
        } else {
            Icons.AutoMirrored.Default.ArrowBack
        },
        contentDescription = null,
    )
}