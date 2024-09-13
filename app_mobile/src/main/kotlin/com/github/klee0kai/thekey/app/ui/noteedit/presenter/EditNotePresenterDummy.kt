package com.github.klee0kai.thekey.app.ui.noteedit.presenter

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.engine.model.DecryptedOtpNote
import com.github.klee0kai.thekey.app.ui.noteedit.model.EditNoteState
import com.github.klee0kai.thekey.app.ui.noteedit.model.EditTabs
import com.github.klee0kai.thekey.app.ui.noteedit.model.initVariants
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

open class EditNotePresenterDummy(
    state: EditNoteState = EditNoteState()
) : EditNotePresenter {

    val scope = DI.defaultThreadScope()
    override val state = MutableStateFlow(state)

    override fun init(
        tab: EditTabs?,
        prefilledNote: DecryptedNote?,
        prefilledOtp: DecryptedOtpNote?,
    ) = scope.launch {
        state.update { it.initVariants() }
    }

    override fun input(
        block: EditNoteState.() -> EditNoteState,
    ) = scope.launch(start = CoroutineStart.UNDISPATCHED) {
        state.update { it.block() }
    }


}