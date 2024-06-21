package com.github.klee0kai.thekey.core.utils.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.overlay.OverlayContainer
import com.github.klee0kai.thekey.core.ui.devkit.theme.DefaultThemes
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate

@DebugOnly
@Composable
fun DebugDarkScreenPreview(
    layoutDirection: LayoutDirection = LayoutDirection.Ltr,
    content: @Composable () -> Unit
) {
    EdgeToEdgeTemplate {
        CompositionLocalProvider(
            LocalLayoutDirection provides layoutDirection,
        ) {
            AppTheme(
                theme = DefaultThemes.darkTheme,
            ) {
                OverlayContainer {
                    content()
                }
            }
        }
    }
}

@DebugOnly
@Composable
fun DebugDarkContentPreview(
    layoutDirection: LayoutDirection = LayoutDirection.Ltr,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalLayoutDirection provides layoutDirection,
    ) {
        AppTheme(
            theme = DefaultThemes.darkTheme,
        ) {
            OverlayContainer {
                content()
            }
        }
    }
}