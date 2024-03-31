package com.github.klee0kai.thekey.app.ui.designkit.color

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

interface CommonColorScheme {

    val isDarkScheme: Boolean get() = false

    val statusBarColor: Color get() = androidColorScheme.background

    val colorsGroupCollection: List<SurfaceScheme>

    /**
     * Read more
     * https://m3.material.io/styles/color/choosing-a-scheme
     */
    val androidColorScheme: ColorScheme

    fun surfaceScheme(group: ColoredStorageGroup): SurfaceScheme

}

data class SurfaceScheme(
    val surfaceColor: Color,
    val onSurfaceColor: Color,
)

enum class ColoredStorageGroup {
    NOCOLOR, VIOLET, TURQUOISE, PINK, ORANGE, CORAL
}