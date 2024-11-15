package com.github.klee0kai.thekey.core.ui.devkit.color

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import com.github.klee0kai.thekey.core.ui.devkit.color.DarkColorCollection.background
import com.github.klee0kai.thekey.core.ui.devkit.color.DarkColorCollection.blackColor
import com.github.klee0kai.thekey.core.ui.devkit.color.DarkColorCollection.coral
import com.github.klee0kai.thekey.core.ui.devkit.color.DarkColorCollection.grayColor
import com.github.klee0kai.thekey.core.ui.devkit.color.DarkColorCollection.green
import com.github.klee0kai.thekey.core.ui.devkit.color.DarkColorCollection.lightBackground
import com.github.klee0kai.thekey.core.ui.devkit.color.DarkColorCollection.noColor
import com.github.klee0kai.thekey.core.ui.devkit.color.DarkColorCollection.orange
import com.github.klee0kai.thekey.core.ui.devkit.color.DarkColorCollection.pink
import com.github.klee0kai.thekey.core.ui.devkit.color.DarkColorCollection.surfaceColor
import com.github.klee0kai.thekey.core.ui.devkit.color.DarkColorCollection.turquoise
import com.github.klee0kai.thekey.core.ui.devkit.color.DarkColorCollection.violet
import com.github.klee0kai.thekey.core.ui.devkit.color.DarkColorCollection.whiteColor
import com.github.klee0kai.thekey.core.ui.devkit.color.DarkColorCollection.yellow

internal object DarkColorCollection {
    /**
     * window background
     */
    val background = Color(0xFF1B1D2D)

    /**
     * bottom sheet / cards background
     */
    val lightBackground = Color(0xFF242738)

    /**
     * NavBoard headed
     * input fields, skeletons
     */
    val surfaceColor = Color(0xFF3A3D52)

    val blackColor = Color.Black
    val whiteColor = Color.White
    val grayColor = Color(0xFFB7B7B7)

    val noColor = SurfaceScheme(grayColor, whiteColor)
    val violet = SurfaceScheme(Color(0xFF837AE8), whiteColor)
    val green = SurfaceScheme(Color(0xFF36C817), whiteColor)
    val yellow = SurfaceScheme(Color(0xFFF0EC1C), whiteColor)
    val turquoise = SurfaceScheme(Color(0xFF7AE8E8), whiteColor)
    val pink = SurfaceScheme(Color(0xFFE87AD6), whiteColor)
    val orange = SurfaceScheme(Color(0xFFDC8938), whiteColor)
    val coral = SurfaceScheme(Color(0xFFE87A7A), whiteColor)
}

fun darkCommonColorScheme() = CommonColorScheme(
    isDark = true,
    windowBackgroundColor = background,
    cardsBackground = lightBackground,
    skeletonColor = surfaceColor,
    navigationBoard = NavigationBoardColors(
        headerBackgroundColor = surfaceColor,
        bodyBackgroundColor = Color(0xFF1C1D27),
    ),
    popupMenu = PopupMenuColors(
        surfaceColor = surfaceColor,
        contentColor = grayColor,
        shadowColor = Color.Black.copy(alpha = 0.2f)
    ),
    whiteTextButtonColors = ButtonColors(
        contentColor = whiteColor,
        containerColor = Color.Transparent,
        disabledContainerColor = whiteColor,
        disabledContentColor = Color.Transparent,
    ),
    grayTextButtonColors = ButtonColors(
        contentColor = grayColor,
        containerColor = Color.Transparent,
        disabledContainerColor = grayColor,
        disabledContentColor = Color.Transparent,
    ),
    surfaceSchemas = SurfaceSchemas(
        noColor = noColor,
        violet = violet,
        turquoise = turquoise,
        pink = pink,
        orange = orange,
        coral = coral,
    ),
    textColors = TextColors(
        bodyTextColor = whiteColor,
        hintTextColor = grayColor,
        primaryTextColor = turquoise.surfaceColor,
        secondaryTextColor = orange.surfaceColor,
        errorTextColor = coral.surfaceColor,
    ),
    greenColor = green.surfaceColor,
    yellowColor = yellow.surfaceColor,
    redColor = coral.surfaceColor,
    androidColorScheme = darkColorScheme(
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

        surface = surfaceColor,
        onSurface = whiteColor,
        surfaceVariant = surfaceColor,
        onSurfaceVariant = whiteColor,
        inverseSurface = whiteColor,
        inverseOnSurface = blackColor,
    )
)
