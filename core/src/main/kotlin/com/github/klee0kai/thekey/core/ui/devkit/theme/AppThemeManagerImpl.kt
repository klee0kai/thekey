package com.github.klee0kai.thekey.core.ui.devkit.theme

import androidx.compose.material3.Shapes
import com.github.klee0kai.thekey.core.ui.devkit.color.darkCommonColorScheme
import com.github.klee0kai.thekey.core.ui.devkit.typography.regularAppTypeScheme
import kotlinx.coroutines.flow.MutableStateFlow

class AppThemeManagerImpl : AppThemeManager {

    override val theme = MutableStateFlow(
        AppTheme(
            colorScheme = darkCommonColorScheme(),
            typeScheme = regularAppTypeScheme(),
            shapes = Shapes()
        )
    )

}