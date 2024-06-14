package com.github.klee0kai.thekey.core.ui.devkit.typography

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.github.klee0kai.thekey.core.ui.devkit.typography.RegularAppTypeScheme.hintText
import com.github.klee0kai.thekey.core.ui.devkit.typography.RegularAppTypeScheme.labelLarge
import com.github.klee0kai.thekey.core.ui.devkit.typography.RegularAppTypeScheme.normalText
import com.github.klee0kai.thekey.core.ui.devkit.typography.RegularAppTypeScheme.titleLarge
import com.github.klee0kai.thekey.core.ui.devkit.typography.RegularAppTypeScheme.titleText

internal object RegularAppTypeScheme {
    val appFontFamily = FontFamily.SansSerif

    val titleLarge = TextStyle(
        fontFamily = appFontFamily,
        fontSize = 16.sp,
        lineHeight = 21.sp,
        fontWeight = FontWeight.Bold,
    )

    val titleText = TextStyle(
        fontFamily = appFontFamily,
        fontSize = 20.sp,
        lineHeight = 23.sp,
    )

    val normalText = TextStyle(
        fontFamily = appFontFamily,
        fontSize = 14.sp,
        lineHeight = 18.sp,
    )

    val hintText = TextStyle(
        fontFamily = appFontFamily,
        fontSize = 12.sp,
        lineHeight = 14.sp,
        fontWeight = FontWeight.Medium
    )

    val labelLarge = TextStyle(
        fontFamily = appFontFamily,
        fontSize = 14.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Bold,
    )


}

fun regularAppTypeScheme() = TypeScheme(
    typography = Typography(
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

        labelLarge = labelLarge,
        labelMedium = normalText,
        labelSmall = hintText,
    )
)

