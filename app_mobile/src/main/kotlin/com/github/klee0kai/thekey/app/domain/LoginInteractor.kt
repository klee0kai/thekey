package com.github.klee0kai.thekey.app.domain

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import kotlinx.coroutines.async

class LoginInteractor {

    val scope = DI.defaultThreadScope()

    fun login(identifier: StorageIdentifier, passw: String) = scope.async {
        val engine = DI.cryptStorageEngineSafeLazy(identifier)
        val noteRep = DI.notesRepLazy(identifier)
        val groupRepLazy = DI.groupRepLazy(identifier)

        noteRep().clear()
        groupRepLazy().clear()

        engine().login(passw)

        noteRep().loadNotes()
        groupRepLazy().loadGroups()
        Unit
    }

    fun unlogin(identifier: StorageIdentifier) = scope.async {
        val engine = DI.cryptStorageEngineSafeLazy(identifier)
        val noteRep = DI.notesRepLazy(identifier)
        val groupRepLazy = DI.groupRepLazy(identifier)

        noteRep().clear()
        groupRepLazy().clear()

        engine().unlogin()

        Unit
    }


}