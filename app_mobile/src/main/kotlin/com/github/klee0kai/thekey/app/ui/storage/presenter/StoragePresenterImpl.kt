package com.github.klee0kai.thekey.app.ui.storage.presenter

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.navigation.editNoteDest
import com.github.klee0kai.thekey.app.ui.navigation.model.EditNoteGroupDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.QRCodeScanDestination
import com.github.klee0kai.thekey.app.ui.storage.model.SearchState
import com.github.klee0kai.thekey.app.ui.storage.model.StorageItem
import com.github.klee0kai.thekey.app.ui.storage.model.group
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.domain.model.feature.PaidFeature
import com.github.klee0kai.thekey.core.domain.model.feature.PaidLimits
import com.github.klee0kai.thekey.core.domain.model.otpNotes
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.ui.navigation.navigate
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import com.github.klee0kai.thekey.core.R as CoreR

open class StoragePresenterImpl(
    val storageIdentifier: StorageIdentifier,
) : StoragePresenter {

    private val notesInteractor = DI.notesInteractorLazy(storageIdentifier)
    private val otpNotesInteractor = DI.otpNotesInteractorLazy(storageIdentifier)
    private val groupsInteractor = DI.groupsInteractorLazy(storageIdentifier)
    private val predefinedNoteGroupsInteractor =
        DI.predefinedNoteGroupsInteractor(storageIdentifier)
    private val billing = DI.billingInteractor()

    private val scope = DI.defaultThreadScope()

    override val searchState = MutableStateFlow(SearchState())
    override val selectedGroupId = MutableStateFlow<Long?>(null)

    private val sortedStorageItems = StoragePresenterHelper
        .sortedStorageItemsFlow(notesInteractor, otpNotesInteractor)
        .flowOn(DI.defaultDispatcher())

    override val filteredColorGroups = flow<List<ColorGroup>> {
        combine(
            flow = predefinedNoteGroupsInteractor().predefinedGroups,
            flow2 = groupsInteractor().groups,
        ) { predefined, regular -> predefined + regular.sortedBy { it.sortableFlatText() } }
            .collect(this@flow)
    }.flowOn(DI.defaultDispatcher())

    override val filteredItems = flow<List<StorageItem>> {
        combine(
            flow = searchState,
            flow2 = selectedGroupId,
            flow3 = sortedStorageItems,
            transform = { search, selectedGroup, items ->
                val filter = search.searchText
                var filtList = items
                if (selectedGroup == ColorGroup.otpNotes().id) {
                    filtList = filtList.filter { it.otp != null }
                } else if (selectedGroup != null) {
                    filtList = filtList.filter { it.group.id == selectedGroup }
                }
                if (filter.isNotBlank()) filtList = filtList.filter { it.filterBy(filter) }
                filtList
            }
        ).collect(this@flow)
    }.flowOn(DI.defaultDispatcher())

    override fun searchFilter(
        newParams: SearchState,
    ) = scope.launch(start = CoroutineStart.UNDISPATCHED) {
        searchState.value = newParams
    }

    override fun selectGroup(groupId: Long) = scope.launch {
        if (selectedGroupId.value == groupId) {
            selectedGroupId.value = null
        } else {
            selectedGroupId.value = groupId
        }
    }

    override fun setColorGroup(
        notePt: Long,
        groupId: Long,
    ) = scope.launch {
        val oldNoteGroupId = notesInteractor().notes
            .firstOrNull()
            ?.firstOrNull { it.id == notePt }
            ?.group
            ?.id
            ?: return@launch
        if (oldNoteGroupId == groupId) {
            notesInteractor().setNotesGroup(listOf(notePt), 0)
        } else {
            notesInteractor().setNotesGroup(listOf(notePt), groupId)
        }
    }

    override fun setOtpColorGroup(
        otpNotePtr: Long,
        groupId: Long,
    ) = scope.launch {
        val oldNoteGroupId = otpNotesInteractor().otpNotes
            .firstOrNull()
            ?.firstOrNull { it.id == otpNotePtr }
            ?.group
            ?.id
            ?: return@launch
        if (oldNoteGroupId == groupId) {
            otpNotesInteractor().setOtpNotesGroup(listOf(otpNotePtr), 0)
        } else {
            otpNotesInteractor().setOtpNotesGroup(listOf(otpNotePtr), groupId)
        }
    }

    override fun scanNewOtpQRCode(
        router: AppRouter?,
    ) = scope.launch {
        val otpUrl = router?.navigate<String>(QRCodeScanDestination)?.firstOrNull() ?: return@launch
        val otp = otpNotesInteractor().otpNoteFromUrl(otpUrl) ?: return@launch
        router.navigate(storageIdentifier.editNoteDest(prefilledOtpNote = otp))
    }

    override fun deleteGroup(id: Long) = scope.launch {
        groupsInteractor().removeGroup(id)
    }

    override fun addNewNoteGroup(appRouter: AppRouter?) = scope.launch {
        val currentColorGroups = filteredColorGroups.firstOrNull()?.size ?: 0
        if (billing.isAvailable(PaidFeature.UNLIMITED_NOTE_GROUPS)
            || currentColorGroups < PaidLimits.PAID_NOTE_GROUPS_LIMIT
        ) {
            appRouter?.navigate(EditNoteGroupDestination(storageIdentifier))
        } else {
            appRouter?.snack(CoreR.string.limited_in_free_version)
        }
    }

}