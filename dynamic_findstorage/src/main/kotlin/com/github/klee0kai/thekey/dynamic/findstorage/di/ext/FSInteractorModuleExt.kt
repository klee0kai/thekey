package com.github.klee0kai.thekey.dynamic.findstorage.di.ext

import com.github.klee0kai.thekey.core.di.modules.CoreInteractorsModule
import com.github.klee0kai.thekey.core.domain.StartupInteractor

class FSInteractorModuleExt(
    private val origin: CoreInteractorsModule,
) : CoreInteractorsModule by origin {

    override fun startupInteractor(): StartupInteractor {
        TODO("Not yet implemented")
    }

}