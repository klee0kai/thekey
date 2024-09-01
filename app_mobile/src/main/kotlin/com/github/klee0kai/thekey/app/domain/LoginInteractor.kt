package com.github.klee0kai.thekey.app.domain

import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.data.mapping.toColoredStorage
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.engine.model.createConfig
import com.github.klee0kai.thekey.core.di.identifiers.FileIdentifier
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.domain.model.ColoredStorage
import com.github.klee0kai.thekey.core.domain.model.feature.PaidFeature
import com.github.klee0kai.thekey.core.domain.model.feature.PaidLimits
import com.github.klee0kai.thekey.core.utils.common.MutexState
import com.github.klee0kai.thekey.core.utils.common.asyncSafe
import com.github.klee0kai.thekey.core.utils.common.launch
import com.github.klee0kai.thekey.core.utils.common.stateFlow
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
    private val rep = DI.authorizedRepLazy()
    private val billing = DI.billingInteractor()
    private val storagesRep = DI.storagesRepositoryLazy()
    private val settingsRep = DI.settingsRepositoryLazy()

    val authorizedStorages = flow {
        val foundStorageRep = storagesRep()
        rep().authorizedStorages
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
    ) = scope.asyncSafe(globalRunDesc = R.string.logining) {
        var identifier = storageIdentifier
        if (identifier.version == 0) {
            identifier = identifier.copy(version = settingsRep().newStorageVersion())
        }

        if (!billing.isAvailable(PaidFeature.UNLIMITED_AUTHORIZED_STORAGES)) {
            rep().authorizedStorages
                .firstOrNull()
                ?.filterIndexed { index, _ -> index + 1 >= PaidLimits.PAID_AUTHORIZED_STORAGE_LIMITS }
                ?.forEach { logout(it).join() }
        }

        val engine = DI.cryptStorageEngineSafeLazy(identifier)
        val notesInteractor = DI.notesInteractorLazy(identifier)
        val otpNotesInteractor = DI.otpNotesInteractorLazy(identifier)
        val groupsInteractor = DI.groupsInteractorLazy(identifier)
        val genPasswInteractor = DI.genPasswInteractorLazy(identifier)

        notesInteractor().clearCache()
        otpNotesInteractor().clearCache()
        groupsInteractor().clearCache()
        genPasswInteractor().clearCache()

        val createConfig = settingsRep().encryptionComplexity().createConfig()

        File(storageIdentifier.path).parentFile?.mkdirs()
        engine().login(passw, createConfig)

        groupsInteractor().loadGroups()
        if (!ignoreLoginned) rep().auth(identifier)

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
        val genPasswInteractor = DI.genPasswInteractorLazy(identifier)

        notesInteractor().clearCache()
        otpNotesInteractor().clearCache()
        groupsInteractor().clearCache()
        genPasswInteractor().clearCache()

        engine().unlogin()
        rep().logout(identifier)
    }

    fun logoutAll() = scope.launch {
        Timber.d("logout all")
        rep().authorizedStorages.firstOrNull()
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