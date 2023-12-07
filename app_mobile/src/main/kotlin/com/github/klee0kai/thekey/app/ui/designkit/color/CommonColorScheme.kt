package com.github.klee0kai.thekey.app.ui.designkit.color

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

interface CommonColorScheme {

    val isDarkScheme: Boolean get() = false

    val violetPrimary: Color
    val turquoisePrimary: Color
    val pinkPrimary: Color
    val orangePrimary: Color
    val coralPrimary: Color

    val primaryBackground: Color
    val secondBackground: Color

    val firstElement: Color
    val secondElement: Color

    val statusBarColor: Color get() = primaryBackground

    val androidColorScheme: ColorScheme


}