package com.github.klee0kai.thekey.app.domain

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.core.domain.model.ColoredStorage
import kotlinx.coroutines.launch

class EditStorageInteractor {

    private val scope = DI.defaultThreadScope()
    private val rep = DI.storagesRepositoryLazy()

    fun createStorage(storage: ColoredStorage) = scope.launch {
        throw IllegalStateException()

    }

    fun moveStorage(from: String, storage: ColoredStorage) = scope.launch {
        throw IllegalStateException()

    }

    fun setStorage(storage: ColoredStorage) = scope.launch {
        throw IllegalStateException()

    }


}