package com.github.klee0kai.thekey.app.ui.changepassw.presenter

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.changepassw.model.ChangePasswordStorageState
import com.github.klee0kai.thekey.app.ui.storage.model.StorageItem
import com.github.klee0kai.thekey.app.ui.storage.model.sortableFlatText
import com.github.klee0kai.thekey.app.ui.storage.model.storageItem
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.utils.common.launch
import com.github.klee0kai.thekey.core.utils.common.launchIfNotStarted
import com.github.klee0kai.thekey.core.utils.common.launchLatest
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.lang.ref.SoftReference
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.seconds

open class ChangeStoragePasswordPresenterImpl(
    private val originStorageIdentifier: StorageIdentifier,
) : ChangeStoragePasswordPresenter {

    private val scope = DI.defaultThreadScope()
    private val interactor = DI.editStorageInteractorLazy()
    private val loginInteractor = DI.loginInteractorLazy()

    override val state = MutableStateFlow(ChangePasswordStorageState())

    override val sortedStorageItems = MutableStateFlow(emptyList<StorageItem>())

    private val loginnedPasswords = ConcurrentHashMap<Int, SoftReference<List<StorageItem>>>()

    override fun input(
        block: ChangePasswordStorageState.() -> ChangePasswordStorageState,
    ) = scope.launch(start = CoroutineStart.UNDISPATCHED) {
        var newState = block.invoke(state.value)
        newState = newState.copy(
            isSaveAvailable = newState.calcSaveAvailable(),
            isConfirmWrong = false,
        )
        state.value = newState
        loadNotesIfNeed()
        confirmWrongIfNeed()
    }

    override fun save(router: AppRouter?) = scope.launch {
        val state = state.value
        if (!state.calcSaveAvailable()) return@launch
        interactor().changePassw(
            path = originStorageIdentifier.path,
            currentPassw = state.currentPassw,
            newPassw = state.newPassw,
        ).join()
    }

    private fun confirmWrongIfNeed() = scope.launchLatest("conf_wrong") {
        delay(5.seconds)
        state.update { state ->
            state.copy(
                isConfirmWrong = state.newPasswConfirm.isNotBlank()
                        && state.newPassw != state.newPasswConfirm
            )
        }
    }

    private fun loadNotesIfNeed() {
        val passw = state.value.currentPassw
        val passwHash = passw.hashCode()
        scope.launchIfNotStarted("notes_${passw.hashCode()}") {
            if (state.value.currentPassw != passw) return@launchIfNotStarted
            if (passw.isBlank()) {
                sortedStorageItems.value = emptyList()
                return@launchIfNotStarted
            }
            val savedList = loginnedPasswords[passwHash]?.get()
            if (!savedList.isNullOrEmpty()) {
                sortedStorageItems.value = savedList
                return@launchIfNotStarted
            }
            sortedStorageItems.value = sortedStorageItems.value.map { storageItem ->
                storageItem.copy(
                    note = storageItem.note?.copy(isLoaded = false),
                    otp = storageItem.otp?.copy(isLoaded = false)
                )
            }
            delay(1.seconds)
            if (state.value.currentPassw != passw) return@launchIfNotStarted

            loginInteractor()
                .login(
                    storageIdentifier = storageIdentifier(passwHash),
                    passw = passw,
                    ignoreLoginned = true,
                )
                .await()

            val notes = notesInteractor(passwHash)
                .loadedNotes
                .firstOrNull()
                ?.toList()
                ?.map { it.storageItem() }
                ?: emptyList()

            val otpNotes = otpNotesInteractor(passwHash)
                .loadedOtpNotes
                .firstOrNull()
                ?.toList()
                ?.map { it.storageItem() }
                ?: emptyList()

            val sortedList = (notes + otpNotes).sortedBy { it.sortableFlatText() }
            loginnedPasswords[passwHash] = SoftReference(sortedList)

            if (state.value.currentPassw == passw) {
                sortedStorageItems.value = sortedList
            }
        }
    }

    override fun clean() = scope.launch {
        val passwds = loginnedPasswords.keys().toList()
        loginnedPasswords.clear()
        passwds.forEach { passw ->
            loginInteractor()
                .unlogin(storageIdentifier(passw))
                .join()
        }
    }

    private fun storageIdentifier(passwHash: Int) =
        originStorageIdentifier.copy(openReason = "ch_${passwHash}")

    private suspend fun notesInteractor(passwHash: Int) =
        DI.notesInteractorLazy(storageIdentifier(passwHash))()

    private suspend fun otpNotesInteractor(passwHash: Int) =
        DI.otpNotesInteractorLazy(storageIdentifier(passwHash))()

}

private fun ChangePasswordStorageState.calcSaveAvailable() = currentPassw.isNotBlank()
        && newPassw.isNotBlank()
        && newPassw == newPasswConfirm