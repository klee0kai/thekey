package com.github.klee0kai.thekey.core.ui.devkit.color

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import com.github.klee0kai.thekey.core.ui.devkit.color.DarkColorCollection.background
import com.github.klee0kai.thekey.core.ui.devkit.color.DarkColorCollection.blackColor
import com.github.klee0kai.thekey.core.ui.devkit.color.DarkColorCollection.coral
import com.github.klee0kai.thekey.core.ui.devkit.color.DarkColorCollection.green
import com.github.klee0kai.thekey.core.ui.devkit.color.DarkColorCollection.lightBackground
import com.github.klee0kai.thekey.core.ui.devkit.color.DarkColorCollection.noColor
import com.github.klee0kai.thekey.core.ui.devkit.color.DarkColorCollection.orange
import com.github.klee0kai.thekey.core.ui.devkit.color.DarkColorCollection.pink
import com.github.klee0kai.thekey.core.ui.devkit.color.DarkColorCollection.turquoise
import com.github.klee0kai.thekey.core.ui.devkit.color.DarkColorCollection.violet
import com.github.klee0kai.thekey.core.ui.devkit.color.DarkColorCollection.whiteColor
import com.github.klee0kai.thekey.core.ui.devkit.color.DarkColorCollection.yellow

internal object DarkColorCollection {
    val lightBackground = Color(0xFF242738)
    val background = Color(0xFF1B1D2D)

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
    navigationBoard = NavigationBoardColors(
        headerSurfaceColor = Color(0xFF3A3D52),
        bodySurfaceColor = Color(0xFF1C1D27),
    ),
    popupMenu = PopupMenuColors(
        surfaceColor = Color(0xFF3A3D52),
        contentColor = Color(0xffB7B7B7),
        shadowColor = Color.Black.copy(alpha = 0.2f)
    ),
    grayTextButtonColors = ButtonColors(
        contentColor = Color(0xffB7B7B7),
        containerColor = Color.Transparent,
        disabledContainerColor = Color(0xffB7B7B7),
        disabledContentColor = Color.Transparent,
    ),
    hintTextColor = Color(0xFFA9A9A9),
    surfaceSchemas = SurfaceSchemas(
        noColor = noColor,
        violet = violet,
        turquoise = turquoise,
        pink = pink,
        orange = orange,
        coral = coral,
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

        surface = lightBackground,
        onSurface = whiteColor,
        surfaceVariant = lightBackground,
        onSurfaceVariant = whiteColor,
        inverseSurface = whiteColor,
        inverseOnSurface = blackColor,
    )
)
