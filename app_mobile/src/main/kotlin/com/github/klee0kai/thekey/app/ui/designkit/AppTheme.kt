package com.github.klee0kai.thekey.app.ui.designkit

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.isDebugInspectorInfoEnabled
import androidx.core.view.WindowCompat
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.updateConfig
import com.valentinilk.shimmer.defaultShimmerTheme

val LocalRouter = compositionLocalOf { DI.router() }
val LocalShimmerTheme = compositionLocalOf { defaultShimmerTheme.copy() }
val LocalColorScheme = compositionLocalOf { DI.theme().colorScheme() }

@Composable
fun AppTheme(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    DI.ctx(view.context)

    val isEditMode = view.isInEditMode || LocalInspectionMode.current || isDebugInspectorInfoEnabled
    val colorScheme = remember { DI.theme().colorScheme() }
    val typeScheme = remember { DI.theme().typeScheme() }

    LaunchedEffect(Unit) {
        DI.updateConfig {
            copy(isViewEditMode = isEditMode)
        }
    }

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.statusBarColor.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = colorScheme.isDarkScheme
        }
    }

    CompositionLocalProvider(
        LocalRouter provides DI.router(),
        LocalShimmerTheme provides defaultShimmerTheme.copy(),
        LocalColorScheme provides DI.theme().colorScheme(),
    ) {
        MaterialTheme(
            colorScheme = colorScheme.androidColorScheme,
            typography = typeScheme.typography,
        ) {
            Surface(
                modifier = modifier,
                color = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground,
            ) {
                content.invoke()
            }
        }
    }
}