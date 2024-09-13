package com.github.klee0kai.thekey.app.ui.noteedit.presenter

import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.engine.model.DecryptedOtpNote
import com.github.klee0kai.thekey.app.ui.noteedit.model.EditNoteState
import com.github.klee0kai.thekey.app.ui.noteedit.model.EditTabs
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.utils.coroutine.emptyJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

interface EditNotePresenter {

    val state: Flow<EditNoteState> get() = emptyFlow()

    fun init(
        tab: EditTabs? = null,
        prefilledNote: DecryptedNote? = null,
        prefilledOtp: DecryptedOtpNote? = null,
    ): Job = emptyJob()

    fun input(block: EditNoteState.() -> EditNoteState): Job = emptyJob()

    fun showHistory(router: AppRouter?): Job = emptyJob()

    fun remove(router: AppRouter?): Job = emptyJob()

    fun scanQRCode(router: AppRouter?): Job = emptyJob()

    fun save(router: AppRouter?): Job = emptyJob()

    fun generate(router: AppRouter?): Job = emptyJob()

}