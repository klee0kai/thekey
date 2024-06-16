package com.github.klee0kai.thekey.dynamic.findstorage.domain

import com.github.klee0kai.thekey.core.domain.StartupInteractor
import com.github.klee0kai.thekey.dynamic.findstorage.di.FSDI
import kotlinx.coroutines.launch

class FSStartupInteractor : StartupInteractor() {

    private val scope = FSDI.defaultThreadScope()
    private val findStorageInteractor = FSDI.findStoragesInteractorLazy()

    override fun appStarted() {
        super.appStarted()
        scope.launch {
            findStorageInteractor().findStoragesIfNeed()
        }
    }
}