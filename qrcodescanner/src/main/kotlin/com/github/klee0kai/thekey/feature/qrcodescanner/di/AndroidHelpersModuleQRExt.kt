package com.github.klee0kai.thekey.feature.qrcodescanner.di

import com.github.klee0kai.thekey.app.di.modules.AndroidHelpersModule
import com.github.klee0kai.thekey.app.ui.navigation.screenresolver.ScreenResolver
import com.github.klee0kai.thekey.feature.qrcodescanner.ui.navigation.ScreenResolverQRExt

class AndroidHelpersModuleQRExt(private val origin: AndroidHelpersModule) : AndroidHelpersModule by origin {

    override fun screenResolver(): ScreenResolver {
        return ScreenResolverQRExt(origin.screenResolver())
    }

}