package com.github.klee0kai.thekey.app.ui.designkit.typography

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

class AppTypeScheme : TypeScheme {

    private val appFontFamily = FontFamily.SansSerif

    private val titleLarge = TextStyle(
        fontFamily = appFontFamily,
        fontSize = 16.sp,
        lineHeight = 21.sp,
        fontWeight = FontWeight.Bold,
    )

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
        fontWeight = FontWeight.Medium
    )

    private val labelLarge = TextStyle(
        fontFamily = appFontFamily,
        fontSize = 14.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Bold,
    )

    override val typography = Typography(
        displayLarge = titleText,
        displayMedium = titleText,
        displaySmall = titleText,

        headlineLarge = titleText,
        headlineMedium = titleText,
        headlineSmall = titleText,

        titleLarge = titleLarge,
        titleMedium = titleText,
        titleSmall = titleText,

        bodyLarge = titleText,
        bodyMedium = normalText,
        bodySmall = hintText,

        /**
         * labelLarge using in Button
         */
        labelLarge = labelLarge,
        labelMedium = normalText,
        labelSmall = hintText,

        )
}