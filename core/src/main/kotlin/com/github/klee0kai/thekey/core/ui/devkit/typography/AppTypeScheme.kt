package com.github.klee0kai.thekey.core.ui.devkit.typography

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.github.klee0kai.thekey.core.ui.devkit.typography.RegularAppTypeScheme.bodyLarge
import com.github.klee0kai.thekey.core.ui.devkit.typography.RegularAppTypeScheme.bodyMedium
import com.github.klee0kai.thekey.core.ui.devkit.typography.RegularAppTypeScheme.bodySmall
import com.github.klee0kai.thekey.core.ui.devkit.typography.RegularAppTypeScheme.hintText
import com.github.klee0kai.thekey.core.ui.devkit.typography.RegularAppTypeScheme.labelLarge
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

    val bodyMedium = TextStyle(
        fontFamily = appFontFamily,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        fontWeight = FontWeight.W500
    )
    val bodySmall = bodyMedium.copy(
        fontSize = 12.sp,
        fontWeight = FontWeight.W400,
    )
    val bodyLarge = bodyMedium.copy(
        fontSize = 16.sp,
        fontWeight = FontWeight.W700,
    )

    val hintText = bodySmall

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

        bodyLarge = bodyLarge,
        bodyMedium = bodyMedium,
        bodySmall = bodySmall,

        labelLarge = labelLarge,
        labelMedium = bodyMedium,
        labelSmall = hintText,
    )
)

