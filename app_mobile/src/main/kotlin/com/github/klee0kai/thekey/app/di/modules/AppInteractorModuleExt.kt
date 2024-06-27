package com.github.klee0kai.thekey.app.di.modules

import com.github.klee0kai.thekey.app.domain.AppAppLifeCycleInteractor
import com.github.klee0kai.thekey.core.di.modules.CoreInteractorsModule
import com.github.klee0kai.thekey.core.domain.AppLifeCycleInteractor

class AppInteractorModuleExt(
    private val origin: CoreInteractorsModule,
) : CoreInteractorsModule by origin {

    override fun lifeCycleInteractor(): AppLifeCycleInteractor = AppAppLifeCycleInteractor(origin.lifeCycleInteractor())

}