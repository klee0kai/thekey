package com.github.klee0kai.thekey.app.ui.editstorage

import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.engine.EditStorageEngine
import com.github.klee0kai.thekey.app.model.Storage
import com.github.klee0kai.thekey.app.ui.navigation.backWithResult
import com.github.klee0kai.thekey.app.ui.navigation.snack
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

open class CreateStoragePresenter {

    protected val navigator = DI.navigator()
    protected val scope = DI.mainThreadScope()
    protected val engine = DI.editStorageEngineLazy()
    protected val rep = DI.foundStoragesRepositoryLazy()

    open val titleRes = R.string.create_storage

    open val storageInfo = flowOf<Storage?>(null)

    open fun save(storage: Storage) {
        scope.launch(DI.defaultDispatcher()) {
            val error = engine().createStorage(storage)
                .let { EditStorageEngine.Error.fromCode(it) }
            val message = when (error) {
                EditStorageEngine.Error.OK -> R.string.save_success
                EditStorageEngine.Error.UNKNOWN_ERROR -> R.string.save_error
                else -> error.stringResId
            }
            if (error == EditStorageEngine.Error.OK) {
                rep().addStorage(storage)
                navigator.backWithResult(Result.success(Unit))
            }

            navigator.snack(message = message)
        }
    }

}