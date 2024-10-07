package com.github.klee0kai.thekey.core.ui.devkit.typography

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.github.klee0kai.thekey.core.ui.devkit.typography.RegularAppTypeScheme.androidBodyLarge
import com.github.klee0kai.thekey.core.ui.devkit.typography.RegularAppTypeScheme.androidBodyMedium
import com.github.klee0kai.thekey.core.ui.devkit.typography.RegularAppTypeScheme.androidBodySmall
import com.github.klee0kai.thekey.core.ui.devkit.typography.RegularAppTypeScheme.androidHintText
import com.github.klee0kai.thekey.core.ui.devkit.typography.RegularAppTypeScheme.androidLabelLarge
import com.github.klee0kai.thekey.core.ui.devkit.typography.RegularAppTypeScheme.androidTitleLarge
import com.github.klee0kai.thekey.core.ui.devkit.typography.RegularAppTypeScheme.androidTitleText
import com.github.klee0kai.thekey.core.ui.devkit.typography.RegularAppTypeScheme.bodySmallText
import com.github.klee0kai.thekey.core.ui.devkit.typography.RegularAppTypeScheme.bodyText
import com.github.klee0kai.thekey.core.ui.devkit.typography.RegularAppTypeScheme.buttonText
import com.github.klee0kai.thekey.core.ui.devkit.typography.RegularAppTypeScheme.headerText
import com.github.klee0kai.thekey.core.ui.devkit.typography.RegularAppTypeScheme.screenHeaderText

internal object RegularAppTypeScheme {
    val appFontFamily = FontFamily.SansSerif

    val androidTitleLarge = TextStyle(
        fontFamily = appFontFamily,
        fontSize = 16.sp,
        lineHeight = 21.sp,
        fontWeight = FontWeight.Bold,
    )

    val androidTitleText = TextStyle(
        fontFamily = appFontFamily,
        fontSize = 20.sp,
        lineHeight = 23.sp,
    )

    val androidBodyMedium = TextStyle(
        fontFamily = appFontFamily,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        fontWeight = FontWeight.W500
    )
    val androidBodySmall = androidBodyMedium.copy(
        fontSize = 12.sp,
        fontWeight = FontWeight.W400,
    )
    val androidBodyLarge = androidBodyMedium.copy(
        fontSize = 16.sp,
        fontWeight = FontWeight.W700,
    )

    val androidHintText = androidBodySmall

    val androidLabelLarge = TextStyle(
        fontFamily = appFontFamily,
        fontSize = 14.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Bold,
    )


    val bodyText = TextStyle(
        fontFamily = appFontFamily,
        fontSize = 14.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.W500,
    )

    val bodySmallText = TextStyle(
        fontFamily = appFontFamily,
        fontSize = 12.sp,
        lineHeight = 14.sp,
        fontWeight = FontWeight.W400,
    )

    val screenHeaderText = TextStyle(
        fontFamily = appFontFamily,
        fontSize = 16.sp,
        lineHeight = 18.sp,
        fontWeight = FontWeight.W700,
    )

    val headerText = TextStyle(
        fontFamily = appFontFamily,
        fontSize = 14.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.W700,
    )

    val buttonText = TextStyle(
        fontFamily = appFontFamily,
        fontSize = 14.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.W700,
    )


}

fun regularAppTypeScheme() = TypeScheme(
    typography = Typography(
        displayLarge = androidTitleText,
        displayMedium = androidTitleText,
        displaySmall = androidTitleText,

        headlineLarge = androidTitleText,
        headlineMedium = androidTitleText,
        headlineSmall = androidTitleText,

        titleLarge = androidTitleLarge,
        titleMedium = androidTitleText,
        titleSmall = androidTitleText,

        bodyLarge = androidBodyLarge,
        bodyMedium = androidBodyMedium,
        bodySmall = androidBodySmall,

        labelLarge = androidLabelLarge,
        labelMedium = androidBodyMedium,
        labelSmall = androidHintText,
    ),

    body = bodyText,
    bodySmall = bodySmallText,
    screenHeader = screenHeaderText,
    header = headerText,
    buttonText = buttonText,
)

