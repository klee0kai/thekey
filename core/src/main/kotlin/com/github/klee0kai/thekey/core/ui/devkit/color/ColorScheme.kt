package com.github.klee0kai.thekey.core.ui.devkit.color

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

interface ColorScheme {

    val isDarkScheme: Boolean get() = false

    val statusBarColor: Color get() = androidColorScheme.background
    val deleteColor: Color
    val navigationBoard: NavigationBoardColors

    val grayTextButtonColors: ButtonColors

    val grayTextFieldColors: TextFieldColors
        @Composable get
    val transparentTextFieldColors: TextFieldColors
        @Composable get
    val hintTextColor: Color

    val noColor: SurfaceScheme
    val violet: SurfaceScheme
    val turquoise: SurfaceScheme
    val pink: SurfaceScheme
    val orange: SurfaceScheme
    val coral: SurfaceScheme

    val colorsGroupCollection: List<SurfaceScheme>

    /**
     * Read more
     * https://m3.material.io/styles/color/choosing-a-scheme
     */
    val androidColorScheme: ColorScheme

    fun surfaceScheme(group: KeyColor): SurfaceScheme

}

data class SurfaceScheme(
    val surfaceColor: Color,
    val onSurfaceColor: Color,
)

enum class KeyColor {
    NOCOLOR, VIOLET, TURQUOISE, PINK, ORANGE, CORAL;

    companion object {
        val colors get() = entries.filter { it != NOCOLOR }
    }
}