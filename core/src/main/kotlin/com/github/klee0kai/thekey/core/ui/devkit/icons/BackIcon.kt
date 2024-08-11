package com.github.klee0kai.thekey.core.ui.devkit.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable

@Composable
fun BackIcon() {
    Icon(
        Icons.AutoMirrored.Default.ArrowBack,
        contentDescription = null,
    )
}