package com.github.klee0kai.thekey.core.ui.devkit.theme

import androidx.compose.material3.Shapes
import androidx.compose.runtime.Stable
import com.github.klee0kai.thekey.core.ui.devkit.color.CommonColorScheme
import com.github.klee0kai.thekey.core.ui.devkit.typography.TypeScheme

@Stable
data class AppTheme(
    val colorScheme: CommonColorScheme,
    val typeScheme: TypeScheme,
    val shapes: Shapes,
)