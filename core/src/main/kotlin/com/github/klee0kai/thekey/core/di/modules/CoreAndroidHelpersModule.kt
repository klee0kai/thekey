package com.github.klee0kai.thekey.core.di.modules

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.stone.annotations.module.Provide
import com.github.klee0kai.thekey.core.feature.DynamicFeaturesManager
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.ui.navigation.screenresolver.ScreenResolver

@Module
interface CoreAndroidHelpersModule {

    @Provide(cache = Provide.CacheType.Weak)
    fun screenResolver(): ScreenResolver = TODO()

    @Provide(cache = Provide.CacheType.Weak)
    fun router(): AppRouter = TODO()

    @Provide(cache = Provide.CacheType.Strong)
    fun dynamicFeaturesManager(): DynamicFeaturesManager = TODO()

}