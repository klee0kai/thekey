package com.github.klee0kai.thekey.app.di.modules

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.stone.annotations.module.Provide
import com.github.klee0kai.thekey.app.BuildConfig
import com.github.klee0kai.thekey.core.feature.DynamicFeaturesManager
import com.github.klee0kai.thekey.app.features.DynamicFeaturesManagerDebug
import com.github.klee0kai.thekey.app.features.DynamicFeaturesManagerGooglePlay
import com.github.klee0kai.thekey.app.perm.PermissionsHelper
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.app.ui.navigation.impl.AppRouterImp
import com.github.klee0kai.thekey.app.ui.navigation.screenresolver.ScreenResolverImpl
import com.github.klee0kai.thekey.core.ui.navigation.screenresolver.ScreenResolver

@Module
interface AndroidHelpersModule {

    @Provide(cache = Provide.CacheType.Weak)
    fun screenResolver(): ScreenResolver = ScreenResolverImpl()

    @Provide(cache = Provide.CacheType.Weak)
    fun permissionsHelper(): PermissionsHelper = PermissionsHelper()

    @Provide(cache = Provide.CacheType.Weak)
    fun router(): AppRouter = AppRouterImp()

    @Provide(cache = Provide.CacheType.Strong)
    fun dynamicFeaturesManager(): DynamicFeaturesManager = when {
        BuildConfig.DEBUG -> DynamicFeaturesManagerDebug()
        else -> DynamicFeaturesManagerGooglePlay()
    }

}