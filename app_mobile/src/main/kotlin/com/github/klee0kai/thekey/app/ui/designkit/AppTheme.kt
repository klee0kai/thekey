package com.github.klee0kai.thekey.app.ui.designkit

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.isDebugInspectorInfoEnabled
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.di.updateConfig
import com.github.klee0kai.thekey.core.domain.model.AppConfig
import com.github.klee0kai.thekey.core.ui.devkit.color.CommonColorScheme
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