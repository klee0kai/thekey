package com.github.klee0kai.thekey.core.di.modules

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.stone.annotations.module.Provide
import com.github.klee0kai.thekey.core.ui.devkit.theme.AppThemeManager
import com.github.klee0kai.thekey.core.ui.devkit.theme.AppThemeManagerImpl

@Module
interface ThemeModule {

    @Provide(cache = Provide.CacheType.Weak)
    fun themeManager(): AppThemeManager = AppThemeManagerImpl()

}