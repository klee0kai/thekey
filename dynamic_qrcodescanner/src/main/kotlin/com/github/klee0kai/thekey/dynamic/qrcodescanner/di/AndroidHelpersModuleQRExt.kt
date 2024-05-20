package com.github.klee0kai.thekey.dynamic.qrcodescanner.di

import com.github.klee0kai.thekey.core.di.modules.CoreAndroidHelpersModule
import com.github.klee0kai.thekey.core.ui.navigation.screenresolver.ScreenResolver
import com.github.klee0kai.thekey.dynamic.qrcodescanner.ui.navigation.ScreenResolverQRExt

class AndroidHelpersModuleQRExt(private val origin: CoreAndroidHelpersModule) : CoreAndroidHelpersModule by origin {

    override fun screenResolver(): ScreenResolver {
        return ScreenResolverQRExt(origin.screenResolver())
    }

}