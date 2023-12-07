package com.github.klee0kai.thekey.app.ui.designkit.color

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

class DarkColorScheme : CommonColorScheme {

    override val isDarkScheme: Boolean = true

    override val violetPrimary = Color(0xFF837AE8)
    override val turquoisePrimary = Color(0xFF7AE8E8)
    override val pinkPrimary = Color(0xFFE87AD6)
    override val orangePrimary = Color(0xFFDC8938)
    override val coralPrimary = Color(0xFFE87A7A)

    override val primaryBackground = Color(0xFF1B1D2D)
    override val secondBackground = Color(0xFF242738)

    override val firstElement = Color(0xFFFFFFFF)
    override val secondElement = Color(0xFFB7B7B7)

    override val androidColorScheme = darkColorScheme(
        primary = turquoisePrimary,
        onPrimary = firstElement,
        primaryContainer = primaryBackground,
        onPrimaryContainer = firstElement,
        surface = secondBackground,
        onSurface = firstElement,
        surfaceVariant = secondBackground,
        onSurfaceVariant = firstElement,
        background = primaryBackground
    )


}