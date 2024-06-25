package com.github.klee0kai.thekey.dynamic.findstorage.di.ext

import com.github.klee0kai.thekey.core.di.modules.CoreInteractorsModule
import com.github.klee0kai.thekey.core.domain.StartupInteractor
import com.github.klee0kai.thekey.dynamic.findstorage.domain.FSStartupInteractorExt

class FSInteractorModuleExt(
    private val origin: CoreInteractorsModule,
) : CoreInteractorsModule by origin {

    override fun startupInteractor(): StartupInteractor = FSStartupInteractorExt(origin.startupInteractor())

}