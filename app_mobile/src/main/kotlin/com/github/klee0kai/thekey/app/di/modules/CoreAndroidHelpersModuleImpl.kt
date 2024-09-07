package com.github.klee0kai.thekey.app.di.modules

import com.github.klee0kai.thekey.app.BuildConfig
import com.github.klee0kai.thekey.app.features.DynamicFeaturesManagerDebug
import com.github.klee0kai.thekey.app.features.DynamicFeaturesManagerGooglePlay
import com.github.klee0kai.thekey.app.ui.navigation.impl.AppRouterImp
import com.github.klee0kai.thekey.app.ui.navigation.screenresolver.ScreenResolverDebugExt
import com.github.klee0kai.thekey.app.ui.navigation.screenresolver.ScreenResolverImpl
import com.github.klee0kai.thekey.core.di.identifiers.ActivityIdentifier
import com.github.klee0kai.thekey.core.di.modules.CoreAndroidHelpersModule
import com.github.klee0kai.thekey.core.domain.model.feature.DynamicFeaturesManager
import com.github.klee0kai.thekey.core.ui.navigation.screenresolver.ScreenResolver

open class CoreAndroidHelpersModuleImpl(
    val origin: CoreAndroidHelpersModule,
) : CoreAndroidHelpersModule by origin {

    override fun screenResolver(): ScreenResolver = ScreenResolverDebugExt(ScreenResolverImpl())

    override fun router(activity: ActivityIdentifier?) = AppRouterImp(activity)

    override fun dynamicFeaturesManager(): DynamicFeaturesManager = when {
        BuildConfig.DEBUG -> DynamicFeaturesManagerDebug()
        else -> DynamicFeaturesManagerGooglePlay()
    }

}