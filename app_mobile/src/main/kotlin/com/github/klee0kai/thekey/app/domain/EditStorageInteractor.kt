package com.github.klee0kai.thekey.app.domain

import com.github.klee0kai.thekey.app.data.mapping.toStorage
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.core.domain.model.ColoredStorage
import com.github.klee0kai.thekey.core.utils.common.asyncResult
import java.io.File

class EditStorageInteractor {

    private val scope = DI.defaultThreadScope()
    private val storagesInteractor = DI.storagesInteractorLazy()
    private val engine = DI.editStorageEngineLazy()

    fun createStorage(storage: ColoredStorage) = scope.asyncResult {
        File(storage.path).parentFile?.mkdirs()
        val error = engine().createStorage(storage.toStorage())
        assert(error == 0) { "error create storage" }
        storagesInteractor().setStorage(storage).join()
    }

    fun moveStorage(from: String, storage: ColoredStorage) = scope.asyncResult {
        File(storage.path).parentFile?.mkdirs()
        var error = engine().move(from, storage.path)
        assert(error == 0) { "error move storage" }
        error = engine().editStorage(storage.toStorage())
        assert(error == 0) { "error edit storage" }

        storagesInteractor().setStorage(storage).join()
    }

    fun setStorage(storage: ColoredStorage) = scope.asyncResult {
        var error = engine().editStorage(storage.toStorage())
        assert(error == 0) { "error edit storage" }

        storagesInteractor().setStorage(storage).join()
    }


}