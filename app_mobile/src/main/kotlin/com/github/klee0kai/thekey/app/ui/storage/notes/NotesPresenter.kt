package com.github.klee0kai.thekey.app.ui.storage.notes

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

class NotesPresenter(
    val storagePath: String = "",
) {
    private val engine = DI.cryptStorageEngineLazy(StorageIdentifier(storagePath))
    private val scope = DI.mainThreadScope()

    fun notes(): Deferred<List<DecryptedNote>> = scope.async {
        engine().notes().toList()
    }

}