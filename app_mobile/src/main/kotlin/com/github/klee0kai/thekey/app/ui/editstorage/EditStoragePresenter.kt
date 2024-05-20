package com.github.klee0kai.thekey.app.ui.editstorage

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.engine.model.Storage
import com.github.klee0kai.thekey.core.R
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

open class EditStoragePresenter(
    private val storagePath: String,
) : CreateStoragePresenter() {

    override val titleRes = R.string.edit_storage

    override val storageInfo = flow<Storage?> {
        emit(engine().findStorageInfo(storagePath))
    }

    override fun save(storage: Storage) {
        scope.launch(DI.defaultDispatcher()) {
            engine().editStorage(storage)
        }
    }

}