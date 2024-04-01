package com.github.klee0kai.thekey.app.ui.designkit.color

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

class DarkColorScheme : CommonColorScheme {

    override val isDarkScheme: Boolean = true

    private val lightBackground = Color(0xFF242738)
    private val background = Color(0xFF1B1D2D)

    private val blackColor = Color.Black
    private val whiteColor = Color.White
    private val grayColor = Color(0xFFB7B7B7)

    override val noColor = SurfaceScheme(grayColor, whiteColor)
    override val violet = SurfaceScheme(Color(0xFF837AE8), whiteColor)
    override val turquoise = SurfaceScheme(Color(0xFF7AE8E8), whiteColor)
    override val pink = SurfaceScheme(Color(0xFFE87AD6), whiteColor)
    override val orange = SurfaceScheme(Color(0xFFDC8938), whiteColor)
    override val coral = SurfaceScheme(Color(0xFFE87A7A), whiteColor)


    override val colorsGroupCollection = listOf(violet, turquoise, pink, orange, coral)

    override val androidColorScheme = darkColorScheme(
        primary = turquoise.surfaceColor,
        onPrimary = whiteColor,
        secondary = orange.surfaceColor,
        onSecondary = whiteColor,
        tertiary = orange.surfaceColor,
        onTertiary = whiteColor,

        primaryContainer = turquoise.surfaceColor,
        onPrimaryContainer = whiteColor,
        secondaryContainer = orange.surfaceColor,
        onSecondaryContainer = whiteColor,
        tertiaryContainer = orange.surfaceColor,
        onTertiaryContainer = whiteColor,

        background = background,
        onBackground = whiteColor,
        outline = whiteColor,

        surface = lightBackground,
        onSurface = whiteColor,
        surfaceVariant = lightBackground,
        onSurfaceVariant = whiteColor,
        inverseSurface = whiteColor,
        inverseOnSurface = blackColor,
    )

    override fun surfaceScheme(group: KeyColor): SurfaceScheme =
        when (group) {
            KeyColor.NOCOLOR -> noColor
            KeyColor.VIOLET -> violet
            KeyColor.TURQUOISE -> turquoise
            KeyColor.PINK -> pink
            KeyColor.ORANGE -> orange
            KeyColor.CORAL -> coral
        }

}