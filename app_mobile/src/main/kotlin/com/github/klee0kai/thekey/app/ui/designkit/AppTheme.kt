package com.github.klee0kai.thekey.app.ui.designkit

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import com.github.klee0kai.thekey.app.domain.model.AppConfig
import com.github.klee0kai.thekey.app.ui.designkit.color.CommonColorScheme
import com.github.klee0kai.thekey.app.ui.navigation.AppRouter
import com.valentinilk.shimmer.ShimmerTheme
import com.valentinilk.shimmer.defaultShimmerTheme

val LocalRouter = compositionLocalOf<AppRouter> { error("no router") }
val LocalShimmerTheme = compositionLocalOf<ShimmerTheme> { error("no shimmer theme") }
val LocalColorScheme = compositionLocalOf<CommonColorScheme> { error("no color scheme") }
val LocalAppConfig = compositionLocalOf<AppConfig> { error("no app config") }

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
    remember {
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
        LocalAppConfig provides DI.config(),
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