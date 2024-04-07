package com.github.klee0kai.thekey.app.domain

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import kotlinx.coroutines.async

class LoginInteractor {

    private val scope = DI.defaultThreadScope()

    fun login(identifier: StorageIdentifier, passw: String) = scope.async {
        val engine = DI.cryptStorageEngineSafeLazy(identifier)
        val notesInteractor = DI.notesInteractorLazy(identifier)
        val groupsInteractor = DI.groupsInteractorLazy(identifier)

        notesInteractor().clear()
        groupsInteractor().clear()

        engine().login(passw)

        notesInteractor().loadNotes()
        groupsInteractor().loadGroups()
        Unit
    }

    fun unlogin(identifier: StorageIdentifier) = scope.async {
        val engine = DI.cryptStorageEngineSafeLazy(identifier)
        val notesInteractor = DI.notesInteractorLazy(identifier)
        val groupsInteractor = DI.groupsInteractorLazy(identifier)

        notesInteractor().clear()
        groupsInteractor().clear()

        engine().unlogin()

        Unit
    }


}