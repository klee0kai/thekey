package com.github.klee0kai.thekey.app.di.modules

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.stone.annotations.module.Provide
import com.github.klee0kai.thekey.app.BuildConfig
import com.github.klee0kai.thekey.app.features.DynamicFeaturesManager
import com.github.klee0kai.thekey.app.features.DynamicFeaturesManagerDebug
import com.github.klee0kai.thekey.app.features.DynamicFeaturesManagerGooglePlay
import com.github.klee0kai.thekey.app.perm.PermissionsHelper
import com.github.klee0kai.thekey.app.ui.navigation.AppRouter
import com.github.klee0kai.thekey.app.ui.navigation.impl.AppRouterImp
import com.github.klee0kai.thekey.app.ui.navigation.screenresolver.ScreenResolver
import com.github.klee0kai.thekey.app.ui.navigation.screenresolver.ScreenResolverImpl

@Module
open class AndroidHelpersModule {

    @Provide(cache = Provide.CacheType.Weak)
    open fun screenResolver(): ScreenResolver = ScreenResolverImpl()

    @Provide(cache = Provide.CacheType.Weak)
    open fun permissionsHelper(): PermissionsHelper = PermissionsHelper()

    @Provide(cache = Provide.CacheType.Weak)
    open fun router(): AppRouter = AppRouterImp()

    @Provide(cache = Provide.CacheType.Strong)
    open fun dynamicFeaturesManager(): DynamicFeaturesManager = when {
        BuildConfig.DEBUG -> DynamicFeaturesManagerDebug()
        else -> DynamicFeaturesManagerGooglePlay()
    }

}