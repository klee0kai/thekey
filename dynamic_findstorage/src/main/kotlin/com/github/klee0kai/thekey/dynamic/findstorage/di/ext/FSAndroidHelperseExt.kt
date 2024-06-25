package com.github.klee0kai.thekey.dynamic.findstorage.di.ext

import com.github.klee0kai.thekey.core.di.modules.CoreAndroidHelpersModule
import com.github.klee0kai.thekey.core.ui.navigation.screenresolver.ScreenResolver
import com.github.klee0kai.thekey.dynamic.findstorage.ui.navigation.FSScreenResolverExt

open class FSAndroidHelperseExt(
    private val origin: CoreAndroidHelpersModule,
) : CoreAndroidHelpersModule by origin {

    override fun screenResolver(): ScreenResolver {
        return FSScreenResolverExt(origin.screenResolver())
    }

}