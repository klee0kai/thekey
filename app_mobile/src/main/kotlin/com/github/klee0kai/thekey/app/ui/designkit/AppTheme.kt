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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.isDebugInspectorInfoEnabled
import androidx.core.view.WindowCompat
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.updateConfig
import com.github.klee0kai.thekey.app.domain.model.AppConfig
import com.github.klee0kai.thekey.app.ui.designkit.color.CommonColorScheme
import com.github.klee0kai.thekey.app.ui.navigation.AppRouter
import com.valentinilk.shimmer.LocalShimmerTheme
import com.valentinilk.shimmer.defaultShimmerTheme

val LocalRouter = compositionLocalOf<AppRouter> { error("no router") }
val LocalColorScheme = compositionLocalOf<CommonColorScheme> { error("no color scheme") }
val LocalAppConfig = compositionLocalOf<AppConfig> { error("no app config") }

@Composable
fun AppTheme(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    LocalConfiguration.current
    DI.ctx(LocalContext.current)
    val view = LocalView.current

    val isEditMode = view.isInEditMode || LocalInspectionMode.current || isDebugInspectorInfoEnabled
    val colorScheme = remember { DI.theme().colorScheme() }
    val typeScheme = remember { DI.theme().typeScheme() }
    val shimmer = remember {
        defaultShimmerTheme.copy(
            shaderColors = listOf(
                Color.Unspecified.copy(alpha = .25f),
                Color.Unspecified.copy(alpha = .4f),
                Color.Unspecified.copy(alpha = .25f),
            ),
        )
    }
    remember {
        DI.updateConfig {
            copy(isViewEditMode = isEditMode)
        }
    }

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = colorScheme.statusBarColor.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = colorScheme.isDarkScheme
        }
    }

    CompositionLocalProvider(
        LocalRouter provides DI.router(),
        LocalShimmerTheme provides shimmer,
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