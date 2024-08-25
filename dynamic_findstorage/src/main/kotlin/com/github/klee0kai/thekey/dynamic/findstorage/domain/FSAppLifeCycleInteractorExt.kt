package com.github.klee0kai.thekey.dynamic.findstorage.domain

import com.github.klee0kai.thekey.core.domain.AppLifeCycleInteractor
import com.github.klee0kai.thekey.dynamic.findstorage.di.FSDI
import kotlinx.coroutines.launch

class FSAppLifeCycleInteractorExt(
    val origin: AppLifeCycleInteractor,
) : AppLifeCycleInteractor by origin {

    private val scope = FSDI.defaultThreadScope()
    private val settings = FSDI.fsSettingsRepositoryLazy()
    private val findStorageInteractor = FSDI.findStoragesInteractorLazy()

    override fun appStarted() {
        origin.appStarted()
        scope.launch {
            if (settings().autoSearchEnabled()) {
                findStorageInteractor().findStoragesIfNeed()
            }
        }
    }
}