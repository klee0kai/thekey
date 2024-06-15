package com.github.klee0kai.thekey.dynamic.findstorage

import com.github.klee0kai.thekey.app.di.AppComponent
import com.github.klee0kai.thekey.app.features.model.FeatureLibApi
import com.github.klee0kai.thekey.dynamic.findstorage.di.ext.FSAndroidHelperseExt
import com.github.klee0kai.thekey.dynamic.findstorage.di.ext.FSInteractorModuleExt

@Suppress("unused")
class FindStorageImpl : FeatureLibApi {

    override fun AppComponent.initDI() {
        initCoreInteractorsModule(
            FSInteractorModuleExt(
                coreInteractorsFactory()
            )
        )

        initCoreAndroidHelpersModule(
            FSAndroidHelperseExt(
                coreAndroidHelpersModuleFactory()
            )
        )
    }

}