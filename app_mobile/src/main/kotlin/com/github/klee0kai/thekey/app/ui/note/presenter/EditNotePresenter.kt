package com.github.klee0kai.thekey.app.ui.note.presenter

import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.ui.note.model.EditNoteState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface EditNotePresenter {

    val state: Flow<EditNoteState> get() = flow { }

    fun init(prefilled: DecryptedNote? = null): Job = Job()

    fun input(block: EditNoteState.() -> EditNoteState): Job = Job()

    fun showHistory(): Job = Job()

    fun remove(): Job = Job()

    fun scanQRCode(): Job = Job()

    fun save(): Job = Job()

    fun generate(): Job = Job()

}