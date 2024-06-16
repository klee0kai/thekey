package com.github.klee0kai.thekey.dynamic.findstorage.domain

import com.github.klee0kai.thekey.dynamic.findstorage.di.FSDI
import kotlinx.coroutines.launch

class FindStorageInteractor {

    private val scope = FSDI.defaultThreadScope()

    fun findStoragesIfNeed() = scope.launch {

    }

}