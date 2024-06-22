package com.github.klee0kai.thekey.dynamic.findstorage.domain

import com.github.klee0kai.thekey.core.domain.StartupInteractor
import com.github.klee0kai.thekey.dynamic.findstorage.di.FSDI
import kotlinx.coroutines.launch

class FSStartupInteractorExt(
    val origin: StartupInteractor,
) : StartupInteractor by origin {

    private val scope = FSDI.defaultThreadScope()
    private val findStorageInteractor = FSDI.findStoragesInteractorLazy()

    override fun appStarted() {
        origin.appStarted()
        scope.launch {
            findStorageInteractor().findStoragesIfNeed()
        }
    }
}