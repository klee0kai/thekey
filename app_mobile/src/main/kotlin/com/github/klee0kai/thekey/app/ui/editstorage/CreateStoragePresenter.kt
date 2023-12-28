package com.github.klee0kai.thekey.app.ui.editstorage

import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.model.Storage
import com.github.klee0kai.thekey.app.ui.navigation.snack
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

open class CreateStoragePresenter {

    protected val navigator = DI.navigator()
    protected val scope = DI.mainThreadScope()
    protected val engine = DI.editStorageEngineLazy()

    open val titleRes = R.string.create_storage

    open val storageInfo = flowOf<Storage?>(null)

    open fun save(storage: Storage) {
        scope.launch(DI.defaultDispatcher()) {
            val success = engine().createStorage(storage)
            navigator.snack(message = if (success) R.string.save_success else R.string.save_error)
        }
    }

}