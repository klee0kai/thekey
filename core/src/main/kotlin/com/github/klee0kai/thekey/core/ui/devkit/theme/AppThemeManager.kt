package com.github.klee0kai.thekey.core.ui.devkit.theme

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

interface AppThemeManager {

    val theme: Flow<AppTheme> get() = emptyFlow()

    fun setTheme(theme: AppTheme) = Unit

    /**
     * when changing the theme, theme modifiers are applied by their identifiers
     */
    fun modify(id: String, theme: AppTheme.() -> AppTheme = { this }) = Unit

}