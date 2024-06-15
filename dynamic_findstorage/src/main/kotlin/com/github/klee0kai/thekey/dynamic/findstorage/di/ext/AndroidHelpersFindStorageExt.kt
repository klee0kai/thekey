package com.github.klee0kai.thekey.dynamic.findstorage.di.ext

import com.github.klee0kai.thekey.core.di.modules.CoreAndroidHelpersModule
import com.github.klee0kai.thekey.core.ui.navigation.screenresolver.ScreenResolver

class AndroidHelpersFindStorageExt(
    private val origin: CoreAndroidHelpersModule,
) : CoreAndroidHelpersModule by origin {

    override fun screenResolver(): ScreenResolver {
        return super.screenResolver()
    }

}