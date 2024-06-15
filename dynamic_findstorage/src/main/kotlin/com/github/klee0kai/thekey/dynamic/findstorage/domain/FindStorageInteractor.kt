package com.github.klee0kai.thekey.dynamic.findstorage.domain

import com.github.klee0kai.thekey.core.domain.StartupInteractor
import com.github.klee0kai.thekey.dynamic.findstorage.di.FindStorageDI
import kotlinx.coroutines.launch

class FindStorageInteractor : StartupInteractor() {

    private val scope = FindStorageDI.defaultThreadScope()

    override fun appStarted() {
        super.appStarted()
        scope.launch {

        }
    }
}