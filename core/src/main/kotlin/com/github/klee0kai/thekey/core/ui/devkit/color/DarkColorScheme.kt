package com.github.klee0kai.thekey.core.ui.devkit.color

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

class DarkColorScheme : ColorScheme {

    override val isDarkScheme: Boolean = true

    private val lightBackground = Color(0xFF242738)
    private val background = Color(0xFF1B1D2D)

    private val blackColor = Color.Black
    private val whiteColor = Color.White
    private val grayColor = Color(0xFFB7B7B7)

    override val deleteColor: Color = Color.Red
    override val navigationBoard = NavigationBoardColors(
        headerContainerColor = Color(0xFF3A3D52),
        bodyContentColor = Color(0xFF1C1D27),
    )
    override val grayTextButtonColors = ButtonColors(
        contentColor = Color(0xffB7B7B7),
        containerColor = Color.Transparent,
        disabledContainerColor = Color(0xffB7B7B7),
        disabledContentColor = Color.Transparent,
    )

    override val grayTextFieldColors: TextFieldColors
        @Composable get() = TextFieldDefaults.colors().copy(
            focusedIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        )

    override val transparentTextFieldColors: TextFieldColors
        @Composable get() = grayTextFieldColors.copy(
            disabledContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledTextColor = Color.Transparent,
        )

    override val hintTextColor = Color(0xFFA9A9A9)


    override val noColor = SurfaceScheme(grayColor, whiteColor)
    override val violet = SurfaceScheme(Color(0xFF837AE8), whiteColor)
    override val turquoise = SurfaceScheme(Color(0xFF7AE8E8), whiteColor)
    override val pink = SurfaceScheme(Color(0xFFE87AD6), whiteColor)
    override val orange = SurfaceScheme(Color(0xFFDC8938), whiteColor)
    override val coral = SurfaceScheme(Color(0xFFE87A7A), whiteColor)

    override val androidColorScheme = darkColorScheme(
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

    override val colorsGroupCollection = listOf(violet, turquoise, pink, orange, coral)

    override fun surfaceScheme(group: KeyColor): SurfaceScheme =
        when (group) {
            KeyColor.NOCOLOR -> noColor
            KeyColor.VIOLET -> violet
            KeyColor.TURQUOISE -> turquoise
            KeyColor.PINK -> pink
            KeyColor.ORANGE -> orange
            KeyColor.CORAL -> coral
        }

}