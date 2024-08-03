package com.github.klee0kai.thekey.app.ui.changepassw.presenter

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.engine.model.coloredNote
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

open class ChangeStoragePasswordPresenterImpl(
    private val storageIdentifier: StorageIdentifier,
) : ChangeStoragePasswordPresenter {

    private val scope = DI.defaultThreadScope()
    private val interactor = DI.editStorageInteractorLazy()

    override val state = MutableStateFlow(ChangePasswordStorageState())

    override val sortedStorageItems = MutableStateFlow(emptyList<StorageItem>())
    private var loadedItemPassw: String? = null

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
            path = storageIdentifier.path,
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
        scope.launchIfNotStarted("notes_${passw.hashCode()}") {
            if (loadedItemPassw == passw || state.value.currentPassw != passw) return@launchIfNotStarted
            sortedStorageItems.value = sortedStorageItems.value.map { storageItem ->
                storageItem.copy(
                    note = storageItem.note?.copy(isLoaded = false),
                    otp = storageItem.otp?.copy(isLoaded = false)
                )
            }
            delay(1.seconds)
            if (state.value.currentPassw != passw) return@launchIfNotStarted

            val notes = interactor().notes(storageIdentifier.path, passw)
                .await()
                .getOrNull()
                ?.toList()
                ?.map { it.coloredNote(isLoaded = true).storageItem() }
                ?: emptyList()

            if (state.value.currentPassw != passw) return@launchIfNotStarted

            val otpNotes = interactor().otpNotes(storageIdentifier.path, passw)
                .await()
                .getOrNull()
                ?.toList()
                ?.map { it.coloredNote(isLoaded = true).storageItem() }
                ?: emptyList()

            val sortedList = (notes + otpNotes).sortedBy { it.sortableFlatText() }

            if (state.value.currentPassw == passw) {
                loadedItemPassw = passw
                sortedStorageItems.value = sortedList
            }
        }
    }

}

private fun ChangePasswordStorageState.calcSaveAvailable() = currentPassw.isNotBlank()
        && newPassw.isNotBlank()
        && newPassw == newPasswConfirm