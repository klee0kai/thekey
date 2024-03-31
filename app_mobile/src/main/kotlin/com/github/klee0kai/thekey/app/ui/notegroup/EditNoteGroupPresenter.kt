package com.github.klee0kai.thekey.app.ui.notegroup

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.NoteGroupIdentifier

class EditNoteGroupPresenter(id: NoteGroupIdentifier) {

    private val engine = DI.cryptStorageEngineSafeLazy(id.storageIdentifier)


}