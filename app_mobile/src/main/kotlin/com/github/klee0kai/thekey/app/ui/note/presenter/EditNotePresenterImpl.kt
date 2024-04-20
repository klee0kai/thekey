package com.github.klee0kai.thekey.app.ui.note.presenter

import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.NoteIdentifier
import com.github.klee0kai.thekey.app.domain.model.ColorGroup
import com.github.klee0kai.thekey.app.domain.model.noGroup
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.engine.model.DecryptedOtpNote
import com.github.klee0kai.thekey.app.engine.model.GenPasswParams
import com.github.klee0kai.thekey.app.engine.model.isEmpty
import com.github.klee0kai.thekey.app.ui.navigation.model.AlertDialogDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.ConfirmDialogResult
import com.github.klee0kai.thekey.app.ui.navigation.model.QRCodeScanDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.TextProvider
import com.github.klee0kai.thekey.app.ui.navigation.navigate
import com.github.klee0kai.thekey.app.ui.navigation.storage
import com.github.klee0kai.thekey.app.ui.note.model.EditNoteState
import com.github.klee0kai.thekey.app.ui.note.model.EditNoteState.Companion.otpAlgoVariants
import com.github.klee0kai.thekey.app.ui.note.model.EditNoteState.Companion.otpTypesVariants
import com.github.klee0kai.thekey.app.ui.note.model.EditTabs
import com.github.klee0kai.thekey.app.ui.note.model.decryptedNote
import com.github.klee0kai.thekey.app.ui.note.model.decryptedOtpNote
import com.github.klee0kai.thekey.app.ui.note.model.isValid
import com.github.klee0kai.thekey.app.ui.note.model.updateWith
import com.github.klee0kai.thekey.app.utils.common.TimeFormats
import com.github.klee0kai.thekey.app.utils.common.launchLatest
import com.github.klee0kai.thekey.app.utils.common.launchLatestSafe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditNotePresenterImpl(
    val identifier: NoteIdentifier,
) : EditNotePresenter {

    private val ctx get() = DI.ctx()
    private val dateFormat = TimeFormats.simpleDateFormat()
    private val scope = DI.defaultThreadScope()
    private val router = DI.router()
    private val notesInteractor = DI.notesInteractorLazy(identifier.storage())
    private val otpNotesInteractor = DI.otpNotesInteractorLazy(identifier.storage())
    private val groupsInteractor = DI.groupsInteractorLazy(identifier.storage())

    private var originNote: DecryptedNote? = null
    private var originOtpNote: DecryptedOtpNote? = null
    private var colorGroups: List<ColorGroup> = emptyList()
    override val state = MutableStateFlow(EditNoteState(isSkeleton = true))

    override fun init(
        tab: EditTabs?,
        prefilledNote: DecryptedNote?,
        prefilledOtp: DecryptedOtpNote?,
    ) = scope.launch {
        if (!state.value.isSkeleton) {
            // already inited
            return@launch
        }

        val isEditMode = identifier.notePtr != 0L || identifier.otpNotePtr != 0L

        val initState = EditNoteState(
            isEditMode = isEditMode,
            otpMethodVariants = otpTypesVariants,
            otpMethodSelected = 1,
            otpAlgoVariants = otpAlgoVariants,
            otpAlgoSelected = 0,
            otpInterval = "30",
            otpDigits = "6",
            otpCounter = "0",
        )
        state.update { initState }

        val colorGroupUpdate = launch {
            colorGroups = groupsInteractor().groups
                .first()
                .let { listOf(ColorGroup.noGroup()) + it }
        }

        var prefilledNote = prefilledNote
        var prefilledOtp = prefilledOtp
        if (identifier.notePtr != 0L) {
            originNote = notesInteractor().note(identifier.notePtr).await()
            prefilledNote = originNote
        }
        if (identifier.otpNotePtr != 0L) {
            originOtpNote = otpNotesInteractor().otpNote(identifier.otpNotePtr).await()
            prefilledOtp = originOtpNote
        }

        colorGroupUpdate.join()

        state.update {
            var newState = it
            if (prefilledNote != null) {
                newState = newState.updateWith(
                    note = prefilledNote,
                    colorGroups = colorGroups,
                    dateFormat = dateFormat,
                )
            }
            if (prefilledOtp != null) {
                newState = newState.updateWith(
                    otp = prefilledOtp,
                )
            }
            newState = newState.copy(
                isRemoveAvailable = isEditMode,
                isSkeleton = false,
            )

            newState
        }
    }

    override fun input(block: EditNoteState.() -> EditNoteState) = scope.launch(DI.mainDispatcher()) {
        var newState = block.invoke(state.value)
        if (!newState.isValid()) return@launch

        val isSaveAvailable = when {
            newState.page == EditTabs.Account && originNote == null -> !newState.decryptedNote().isEmpty()
            newState.page == EditTabs.Account && originNote != null -> newState.decryptedNote(originNote!!) != originNote
            newState.page == EditTabs.Otp && originOtpNote == null -> !newState.decryptedOtpNote().isEmpty()
            newState.page == EditTabs.Otp && originOtpNote != null -> newState.decryptedOtpNote(originOtpNote!!) != originOtpNote
            else -> false
        }

        newState = newState.copy(
            isSaveAvailable = isSaveAvailable,
            isRemoveAvailable = newState.isRemoveAvailable && !isSaveAvailable,
        )
        state.value = newState
    }

    override fun showHistory() = scope.launchLatest("hist") {

    }

    override fun remove() = scope.launchLatest("rm") {
        val curState = state.value
        when (curState.page) {
            EditTabs.Account -> {
                val note = originNote ?: return@launchLatest
                val deleteConfirm = router.navigate<ConfirmDialogResult>(
                    AlertDialogDestination(
                        title = TextProvider(R.string.confirm_delete),
                        message = TextProvider(buildString {
                            appendLine(ctx.resources.getString(R.string.confirm_delete_note))
                            appendLine("${note.site} - ${note.login}")
                        }),
                        confirm = TextProvider(R.string.delete),
                        reject = TextProvider(R.string.cancel),
                    )
                ).last()
                if (deleteConfirm == ConfirmDialogResult.CONFIRMED) {
                    notesInteractor().removeNote(note.ptnote)
                    router.back()
                }
            }

            EditTabs.Otp -> {
                val otp = originOtpNote ?: return@launchLatest
                val deleteConfirm = router.navigate<ConfirmDialogResult>(
                    AlertDialogDestination(
                        title = TextProvider(R.string.confirm_delete),
                        message = TextProvider(buildString {
                            appendLine(ctx.resources.getString(R.string.confirm_delete_otp))
                            appendLine("${otp.issuer} - ${otp.name}")
                        }),
                        confirm = TextProvider(R.string.delete),
                        reject = TextProvider(R.string.cancel),
                    )
                ).last()
                if (deleteConfirm == ConfirmDialogResult.CONFIRMED) {
                    otpNotesInteractor().removeOtpNote(otp.ptnote)
                    router.back()
                }
            }
        }
    }

    override fun scanQRCode() = scope.launchLatest("qr") {
        val otpUrl = router.navigate<String>(QRCodeScanDestination).firstOrNull() ?: return@launchLatest
        val otp = otpNotesInteractor().otpNoteFromUrl(otpUrl) ?: return@launchLatest
        input { updateWith(otp) }
    }

    override fun save() = scope.launchLatest("safe") {
        val curState = state.value
        when (curState.page) {
            EditTabs.Account -> {
                val note = curState.decryptedNote(origin = originNote ?: DecryptedNote())
                if (note.isEmpty()) {
                    router.snack(R.string.note_is_empty)
                    return@launchLatest
                }

                val error = notesInteractor().saveNote(note, setAll = true)
                router.back()
            }

            EditTabs.Otp -> {
                val otpNote = curState.decryptedOtpNote(origin = originOtpNote ?: DecryptedOtpNote())
                if (otpNote.isEmpty()) {
                    router.snack(R.string.otpnote_is_empty)
                    return@launchLatest
                }
                val error = otpNotesInteractor().saveOtpNote(otpNote, setAll = true)
                router.back()
            }
        }

    }

    override fun generate() = scope.launchLatestSafe("gen") {
        val newPassw = notesInteractor()
            .generateNewPassw(GenPasswParams(oldPassw = state.value.passw))
            .await()

        input { copy(passw = newPassw) }
    }

}