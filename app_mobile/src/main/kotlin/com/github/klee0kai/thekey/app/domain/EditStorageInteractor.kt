package com.github.klee0kai.thekey.app.domain

import com.github.klee0kai.thekey.app.data.mapping.toStorage
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.engine.model.ChPasswStrategy
import com.github.klee0kai.thekey.app.engine.model.createConfig
import com.github.klee0kai.thekey.core.domain.model.ColoredStorage
import com.github.klee0kai.thekey.core.utils.common.asyncResult
import com.github.klee0kai.thekey.core.utils.common.launch
import com.github.klee0kai.thekey.core.utils.error.FSDuplicateError
import com.github.klee0kai.thekey.core.utils.error.FSNoAccessError
import java.io.File
import com.github.klee0kai.thekey.core.R as CoreR

class EditStorageInteractor {

    private val scope = DI.defaultThreadScope()
    private val settings = DI.settingsRepositoryLazy()
    private val rep = DI.storagesRepositoryLazy()
    private val engine = DI.editStorageEngineSafeLazy()

    fun createStorage(
        storage: ColoredStorage,
    ) = scope.asyncResult(globalRunDesc = CoreR.string.creating_storage) {
        val folder = File(storage.path).parentFile
        folder?.mkdirs()
        if (folder?.canRead() != true || !folder.exists()) throw FSNoAccessError()
        if (File(storage.path).exists()) throw FSDuplicateError()

        val error = engine().createStorage(
            storage = storage.toStorage(),
            createStorageConfig = settings().encryptionComplexity().createConfig(),
        )
        engine().throwError(error)
        rep().setStorage(storage).join()
    }

    fun moveStorage(
        from: String,
        storage: ColoredStorage,
    ) = scope.asyncResult(globalRunDesc = CoreR.string.moving_storage) {
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

    fun changePassw(
        path: String,
        currentPassw: String,
        newPassw: String,
    ) = scope.launch(globalRunDesc = CoreR.string.changing_storage_password) {
        engine().changePassw(path, currentPassw, newPassw)
    }

    fun changePassw(
        path: String,
        strategies: List<ChPasswStrategy>,
    ) = scope.launch(globalRunDesc = CoreR.string.changing_storage_password) {
        engine().changePasswStrategy(path, strategies)
    }

    fun notes(
        path: String,
        passw: String,
    ) = scope.asyncResult {
        engine().notes(path, passw)
    }

    fun otpNotes(
        path: String,
        passw: String,
    ) = scope.asyncResult {
        engine().otpNotes(path, passw)
    }

    fun deleteStorage(
        path: String
    ) = scope.asyncResult(globalRunDesc = CoreR.string.deleting_storage) {
        File(path).delete()
        rep().deleteStorage(path).join()
    }


}