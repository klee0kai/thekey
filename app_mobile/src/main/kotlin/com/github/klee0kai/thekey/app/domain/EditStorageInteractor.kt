package com.github.klee0kai.thekey.app.domain

import com.github.klee0kai.thekey.app.data.mapping.toStorage
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.core.domain.model.ColoredStorage
import com.github.klee0kai.thekey.core.utils.common.asyncResult
import com.github.klee0kai.thekey.core.utils.error.FSDuplicateError
import com.github.klee0kai.thekey.core.utils.error.FSNoAccessError
import java.io.File

class EditStorageInteractor {

    private val scope = DI.defaultThreadScope()
    private val rep = DI.storagesRepositoryLazy()
    private val engine = DI.editStorageEngineSafeLazy()

    fun createStorage(storage: ColoredStorage) = scope.asyncResult {
        val folder = File(storage.path).parentFile
        folder?.mkdirs()
        if (folder?.canRead() != true || !folder.exists()) throw FSNoAccessError()
        if (File(storage.path).exists()) throw FSDuplicateError()

        val error = engine().createStorage(storage.toStorage())
        engine().throwError(error)
        rep().setStorage(storage).join()
    }

    fun moveStorage(from: String, storage: ColoredStorage) = scope.asyncResult {
        File(storage.path).parentFile?.mkdirs()
        var error = engine().move(from, storage.path)
        engine().throwError(error)

        error = engine().editStorage(storage.toStorage())
        engine().throwError(error)

        rep().deleteStorage(from)
        rep().setStorage(storage).join()
    }

    fun setStorage(storage: ColoredStorage) = scope.asyncResult {
        val error = engine().editStorage(storage.toStorage())
        engine().throwError(error)

        rep().setStorage(storage).join()
    }

    fun deleteStorage(path: String) = scope.asyncResult {
        File(path).delete()
        rep().deleteStorage(path).join()
    }


}