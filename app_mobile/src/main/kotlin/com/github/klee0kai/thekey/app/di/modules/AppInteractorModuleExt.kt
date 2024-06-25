package com.github.klee0kai.thekey.app.di.modules

import com.github.klee0kai.thekey.app.domain.AppStartupInteractor
import com.github.klee0kai.thekey.core.di.modules.CoreInteractorsModule
import com.github.klee0kai.thekey.core.domain.StartupInteractor

class AppInteractorModuleExt(
    private val origin: CoreInteractorsModule,
) : CoreInteractorsModule by origin {

    override fun startupInteractor(): StartupInteractor = AppStartupInteractor(origin.startupInteractor())

}