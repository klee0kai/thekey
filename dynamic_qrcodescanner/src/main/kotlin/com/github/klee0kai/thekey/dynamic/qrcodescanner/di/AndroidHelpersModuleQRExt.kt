package com.github.klee0kai.thekey.dynamic.qrcodescanner.di

import com.github.klee0kai.thekey.app.di.modules.AndroidHelpersModule
import com.github.klee0kai.thekey.core.ui.navigation.screenresolver.ScreenResolver
import com.github.klee0kai.thekey.dynamic.qrcodescanner.ui.navigation.ScreenResolverQRExt

class AndroidHelpersModuleQRExt(private val origin: AndroidHelpersModule) : AndroidHelpersModule by origin {

    override fun screenResolver(): ScreenResolver {
        return ScreenResolverQRExt(origin.screenResolver())
    }

}