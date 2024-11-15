package com.github.klee0kai.thekey.core.ui.devkit.color

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Stable
data class CommonColorScheme(
    val isDark: Boolean,
    val windowBackgroundColor: Color,
    /**
     * Bottom sheet/card background
     */
    val cardsBackground: Color,
    val skeletonColor: Color,
    val navigationBoard: NavigationBoardColors,
    val popupMenu: PopupMenuColors,
    val whiteTextButtonColors: ButtonColors,
    val grayTextButtonColors: ButtonColors,
    val textColors: TextColors,
    val greenColor: Color,
    val yellowColor: Color,
    val redColor: Color,
    val surfaceSchemas: SurfaceSchemas,

    val androidColorScheme: ColorScheme,
)
