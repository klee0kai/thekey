package com.github.klee0kai.thekey.core.ui.devkit.color

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Stable
data class CommonColorScheme(
    val isDark: Boolean,
    val deleteColor: Color,
    val windowBackgroundColor: Color,
    val navigationBoard: NavigationBoardColors,
    val popupMenu: PopupMenuColors,
    val grayTextButtonColors: ButtonColors,
    val hintTextColor: Color,
    val surfaceSchemas: SurfaceSchemas,
    val androidColorScheme: ColorScheme,
)
