package com.github.klee0kai.thekey.core.di.modules

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.stone.annotations.module.Provide
import com.github.klee0kai.thekey.core.domain.model.feature.DynamicFeaturesManager
import com.github.klee0kai.thekey.core.ui.devkit.theme.AppThemeManager
import com.github.klee0kai.thekey.core.ui.devkit.theme.AppThemeManagerImpl
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.ui.navigation.screenresolver.ScreenResolver

@Module
interface CoreAndroidHelpersModule {

    @Provide(cache = Provide.CacheType.Weak)
    fun themeManager(): AppThemeManager = AppThemeManagerImpl()

    @Provide(cache = Provide.CacheType.Weak)
    fun screenResolver(): ScreenResolver = object : ScreenResolver {}

    @Provide(cache = Provide.CacheType.Weak)
    fun router(): AppRouter = object : AppRouter {}

    @Provide(cache = Provide.CacheType.Strong)
    fun dynamicFeaturesManager(): DynamicFeaturesManager = object : DynamicFeaturesManager {}

}