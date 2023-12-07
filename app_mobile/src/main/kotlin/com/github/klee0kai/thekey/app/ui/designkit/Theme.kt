package com.github.klee0kai.thekey.app.ui.designkit

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.designkit.theme.Typography

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DI.theme().colorScheme()

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.statusBarColor.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = colorScheme.isDarkScheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme.androidColorScheme,
        typography = Typography,
        content = content
    )
}