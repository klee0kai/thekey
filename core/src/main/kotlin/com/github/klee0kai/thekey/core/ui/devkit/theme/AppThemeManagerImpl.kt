package com.github.klee0kai.thekey.core.ui.devkit.theme

import androidx.compose.material3.Shapes
import com.github.klee0kai.thekey.core.di.CoreDI
import com.github.klee0kai.thekey.core.ui.devkit.color.darkCommonColorScheme
import com.github.klee0kai.thekey.core.ui.devkit.typography.regularAppTypeScheme
import com.github.klee0kai.thekey.core.utils.common.launchSafe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull

class AppThemeManagerImpl : AppThemeManager {

    private val scope = CoreDI.defaultThreadScope()

    private var baseTheme: AppTheme? = null
    private val modifiers = mutableListOf<Pair<String, AppTheme.() -> AppTheme>>()

    private val _themeState = MutableStateFlow<AppTheme?>(null)
    override val theme = _themeState.filterNotNull()

    init {
        baseTheme = AppTheme(
            colorScheme = darkCommonColorScheme(),
            typeScheme = regularAppTypeScheme(),
            shapes = Shapes(),
        )
        updateTheme()
    }

    override fun setTheme(theme: AppTheme) {
        scope.launchSafe {
            baseTheme = theme
            updateTheme()
        }
    }

    override fun modify(id: String, modifier: AppTheme.() -> AppTheme) {
        scope.launchSafe {
            modifiers.removeIf { it.first == id }
            modifiers.add(id to modifier)

            updateTheme()
        }
    }

    private fun updateTheme() {
        var theme = baseTheme ?: return
        modifiers.forEach { (_, modifier) ->
            theme = modifier.invoke(theme)
        }
        _themeState.value = theme
    }

}