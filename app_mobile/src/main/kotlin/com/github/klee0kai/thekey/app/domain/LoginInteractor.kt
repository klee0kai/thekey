package com.github.klee0kai.thekey.app.domain

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.domain.model.ColoredStorage
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.File

class LoginInteractor {

    private val scope = DI.defaultThreadScope()
    private val rep = DI.loginedRepLazy()
    private val storagesRep = DI.storagesRepositoryLazy()
    private val settingsRep = DI.settingsRepositoryLazy()

    val logginedStorages = flow {
        val foundStorageRep = storagesRep()
        rep().logginedStorages
            .map { storages ->
                storages.map { storage ->
                    foundStorageRep.findStorage(storage.path).await()
                        ?: ColoredStorage(path = storage.path)
                }
            }.collect(this)
    }

    fun login(storageIdentifier: StorageIdentifier, passw: String) = scope.async {
        var identifier = storageIdentifier
        if (identifier.version == 0) identifier = identifier.copy(version = settingsRep().newStorageVersion())

        val engine = DI.cryptStorageEngineSafeLazy(identifier)
        val notesInteractor = DI.notesInteractorLazy(identifier)
        val otpNotesInteractor = DI.otpNotesInteractorLazy(identifier)
        val groupsInteractor = DI.groupsInteractorLazy(identifier)

        notesInteractor().clear()
        otpNotesInteractor().clear()
        groupsInteractor().clear()

        File(storageIdentifier.path).parentFile?.mkdirs()
        engine().login(passw)

        notesInteractor().loadNotes()
        otpNotesInteractor().loadOtpNotes()
        groupsInteractor().loadGroups()
        rep().logined(identifier)

        identifier
    }

    fun unlogin(identifier: StorageIdentifier) = scope.async {
        val engine = DI.cryptStorageEngineSafeLazy(identifier)
        val notesInteractor = DI.notesInteractorLazy(identifier)
        val otpNotesInteractor = DI.otpNotesInteractorLazy(identifier)
        val groupsInteractor = DI.groupsInteractorLazy(identifier)

        notesInteractor().clear()
        otpNotesInteractor().clear()
        groupsInteractor().clear()

        engine().unlogin()
        rep().logouted(identifier)
        Unit
    }

}