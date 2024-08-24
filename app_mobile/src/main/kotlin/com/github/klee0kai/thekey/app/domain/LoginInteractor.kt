package com.github.klee0kai.thekey.app.domain

import com.github.klee0kai.thekey.app.data.mapping.toColoredStorage
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.core.di.identifiers.FileIdentifier
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.domain.model.ColoredStorage
import com.github.klee0kai.thekey.core.utils.common.MutexState
import com.github.klee0kai.thekey.core.utils.common.launch
import com.github.klee0kai.thekey.core.utils.common.stateFlow
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.File
import kotlin.time.Duration.Companion.milliseconds

class LoginInteractor {

    private val scope = DI.defaultThreadScope()
    private val rep = DI.loginedRepLazy()
    private val billing = DI.billingInteractor()
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

    fun login(
        storageIdentifier: StorageIdentifier,
        passw: String,
        ignoreLoginned: Boolean = false
    ) = scope.async {
        var identifier = storageIdentifier
        if (identifier.version == 0) {
            identifier = identifier.copy(version = settingsRep().newStorageVersion())
        }

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
        if (!ignoreLoginned) rep().logined(identifier)

        if (storagesRep().findStorage(identifier.path).await() == null) {
            // create storage if not exist
            storagesRep().setStorage(engine().info().toColoredStorage())
        }

        identifier
    }

    fun logout(identifier: StorageIdentifier) = scope.launch {
        // wait no one use the storage
        val fileMutex = DI.fileMutex(FileIdentifier(identifier.path))
        fileMutex.stateFlow()
            .debounce(100.milliseconds)
            .firstOrNull { it.state == MutexState.UNLOCKED }

        val engine = DI.cryptStorageEngineSafeLazy(identifier)
        val notesInteractor = DI.notesInteractorLazy(identifier)
        val otpNotesInteractor = DI.otpNotesInteractorLazy(identifier)
        val groupsInteractor = DI.groupsInteractorLazy(identifier)

        notesInteractor().clear()
        otpNotesInteractor().clear()
        groupsInteractor().clear()

        engine().unlogin()
        rep().logouted(identifier)
    }

    fun logoutAll() = scope.launch {
        Timber.d("logout all")
        rep().logginedStorages.firstOrNull()
            ?.map { DI.fileMutex(FileIdentifier(it.path)).stateFlow() }
            ?.let {
                combine(it) { mutexInfos ->
                    mutexInfos.all { mutexInfo -> mutexInfo.state == MutexState.UNLOCKED }
                }
            }?.debounce(100.milliseconds)
            ?.firstOrNull { it }

        val engine = DI.cryptStorageEngineSafeLazy(StorageIdentifier())

        engine().logoutAll()
        rep().logoutAll()
    }

}