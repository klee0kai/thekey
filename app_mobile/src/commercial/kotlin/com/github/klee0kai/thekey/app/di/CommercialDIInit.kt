package com.github.klee0kai.thekey.app.di

import com.github.klee0kai.feature.billing.di.CommercialInteractorModuleExt

object CommercialDIInit {

    fun AppComponent.initDI() {
        initCoreInteractorsModule(
            CommercialInteractorModuleExt(
                coreInteractorsFactory()
            )
        )
    }

}