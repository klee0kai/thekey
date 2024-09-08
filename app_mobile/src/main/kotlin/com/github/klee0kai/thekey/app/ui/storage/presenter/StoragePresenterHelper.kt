package com.github.klee0kai.thekey.app.ui.storage.presenter

import com.github.klee0kai.thekey.app.domain.NotesInteractor
import com.github.klee0kai.thekey.app.domain.OtpNotesInteractor
import com.github.klee0kai.thekey.app.ui.storage.model.StorageItem
import com.github.klee0kai.thekey.app.ui.storage.model.storageItem
import com.github.klee0kai.thekey.core.di.wrap.AsyncCoroutineProvide
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

object StoragePresenterHelper {

    fun sortedStorageItemsFlow(
        notesInteractor: AsyncCoroutineProvide<NotesInteractor>,
        otpNotesInteractor: AsyncCoroutineProvide<OtpNotesInteractor>,
    ) = flow<List<StorageItem>> {
        combine(
            flow = notesInteractor().notes,
            flow2 = otpNotesInteractor().otpNotes,
            transform = { notes, otpNotes ->
                val allStorageNotes = notes.map { it.storageItem() } +
                        otpNotes.map { it.storageItem() }

                allStorageNotes
                    .sortedBy { it.sortableFlatText() }
            }).collect(this)
    }

}