package com.github.klee0kai.thekey.dynamic.findstorage.di.ext

import com.github.klee0kai.thekey.core.di.modules.CoreInteractorsModule
import com.github.klee0kai.thekey.core.domain.AppLifeCycleInteractor
import com.github.klee0kai.thekey.dynamic.findstorage.domain.FSAppLifeCycleInteractorExt

class FSInteractorModuleExt(
    private val origin: CoreInteractorsModule,
) : CoreInteractorsModule by origin {

    override fun lifeCycleInteractor(): AppLifeCycleInteractor = FSAppLifeCycleInteractorExt(origin.lifeCycleInteractor())

}