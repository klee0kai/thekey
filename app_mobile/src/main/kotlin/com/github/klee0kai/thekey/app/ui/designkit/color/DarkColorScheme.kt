package com.github.klee0kai.thekey.app.ui.designkit.color

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

class DarkColorScheme : CommonColorScheme {

    override val isDarkScheme: Boolean = true

    val violetColor = Color(0xFF837AE8)
    val turquoiseColor = Color(0xFF7AE8E8)
    val pinkColor = Color(0xFFE87AD6)
    val orangeColor = Color(0xFFDC8938)
    val coralColor = Color(0xFFE87A7A)

    val lightBackground = Color(0xFF242738)
    val background = Color(0xFF1B1D2D)

    val whiteColor = Color(0xFFFFFFFF)
    val grayColor = Color(0xFFB7B7B7)

    override val colorsGroupCollection: List<Color> = listOf(
        violetColor,
        turquoiseColor,
        pinkColor,
        orangeColor,
        coralColor,
    )

    override val androidColorScheme = darkColorScheme(
        primary = turquoiseColor,
        onPrimary = whiteColor,
        secondary = orangeColor,
        onSecondary = whiteColor,
        tertiary = orangeColor,
        onTertiary = whiteColor,

        primaryContainer = lightBackground,
        onPrimaryContainer = whiteColor,
        secondaryContainer = lightBackground,
        onSecondaryContainer = whiteColor,
        tertiaryContainer = lightBackground,
        onTertiaryContainer = whiteColor,

        background = background,
        onBackground = whiteColor,
        outline = whiteColor,

        surface = lightBackground,
        onSurface = whiteColor,
        surfaceVariant = lightBackground,
        onSurfaceVariant = whiteColor,
        inverseSurface = grayColor,
        inverseOnSurface = whiteColor,
    )


}