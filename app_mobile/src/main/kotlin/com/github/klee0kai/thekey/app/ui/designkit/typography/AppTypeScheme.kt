package com.github.klee0kai.thekey.app.ui.designkit.typography

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp

class AppTypeScheme : TypeScheme {

    private val appFontFamily = FontFamily.SansSerif

    private val titleText = TextStyle(
        fontFamily = appFontFamily,
        fontSize = 20.sp,
        lineHeight = 23.sp,
    )

    private val normalText = TextStyle(
        fontFamily = appFontFamily,
        fontSize = 14.sp,
        lineHeight = 18.sp,
    )

    private val hintText = TextStyle(
        fontFamily = appFontFamily,
        fontSize = 12.sp,
        lineHeight = 14.sp,
    )

    override val typography = Typography(
        displayLarge = titleText,
        displayMedium = titleText,
        displaySmall = titleText,

        headlineLarge = titleText,
        headlineMedium = titleText,
        headlineSmall = titleText,

        titleLarge = titleText,
        titleMedium = titleText,
        titleSmall = titleText,

        bodyLarge = titleText,
        bodyMedium = normalText,
        bodySmall = hintText,

        labelLarge = normalText,
        labelMedium = normalText,
        labelSmall = hintText,

        )
}