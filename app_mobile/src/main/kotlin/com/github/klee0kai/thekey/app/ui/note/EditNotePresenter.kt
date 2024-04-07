package com.github.klee0kai.thekey.app.ui.note

import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.NoteIdentifier
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.engine.model.GenPasswParams
import com.github.klee0kai.thekey.app.engine.model.isEmpty
import com.github.klee0kai.thekey.app.model.ColorGroup
import com.github.klee0kai.thekey.app.model.noGroup
import com.github.klee0kai.thekey.app.ui.navigation.model.AlertDialogDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.ConfirmDialogResult
import com.github.klee0kai.thekey.app.ui.navigation.model.TextProvider
import com.github.klee0kai.thekey.app.ui.navigation.navigate
import com.github.klee0kai.thekey.app.ui.navigation.storage
import com.github.klee0kai.thekey.app.ui.note.model.EditNoteState
import com.github.klee0kai.thekey.app.ui.note.model.EditTabs
import com.github.klee0kai.thekey.app.ui.note.model.decryptedNote
import com.github.klee0kai.thekey.app.ui.note.model.isValid
import com.github.klee0kai.thekey.app.utils.common.TimeFormats
import com.github.klee0kai.thekey.app.utils.common.launchLatest
import com.github.klee0kai.thekey.app.utils.common.launchLatestSafe
import com.github.klee0kai.thekey.app.utils.lazymodel.fullValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.concurrent.TimeUnit

class EditNotePresenter(
    val identifier: NoteIdentifier,
) {
    private val otpTypesVariants = listOf("HOTP", "TOTP", "YaOTP")
    private val otpAlgoVariants = listOf("SHA1", "SHA256", "SHA512")

    private val ctx get() = DI.app()
    private val dateFormat = TimeFormats.simpleDateFormat()
    private val scope = DI.defaultThreadScope()
    private val router = DI.router()
    private val interactor = DI.notesInteractorLazy(identifier.storage())
    private val groupsInteractor = DI.groupsInteractorLazy(identifier.storage())

    private var originNote: DecryptedNote? = null
    private var colorGroups: List<ColorGroup> = emptyList()
    val state = MutableStateFlow(EditNoteState(isSkeleton = true))

    fun init(prefilled: DecryptedNote? = null) = scope.launch {
        val isEditMode = identifier.notePtr != 0L
        val initState = EditNoteState(
            isEditMode = isEditMode,
            isSkeleton = true,
            otpTypeVariants = otpTypesVariants,
            otpTypeSelected = 1,
            otpAlgoVariants = otpAlgoVariants,
            otpAlgoSelected = 0,
            otpPeriod = "30",
            otpDigits = "6",
            otpCounter = "0",
        )
        state.update { initState }

        var prefilled = prefilled
        if (identifier.notePtr != 0L) {
            originNote = interactor().note(identifier.notePtr).await()
            prefilled = originNote
        }

        colorGroups = groupsInteractor().groups
            .first()
            .map { it.fullValue() }
            .let { listOf(ColorGroup.noGroup()) + it }

        state.update { note ->
            note.copy(
                isRemoveAvailable = isEditMode,
                isSkeleton = false,
                siteOrIssuer = prefilled?.site ?: "",
                login = prefilled?.login ?: "",
                passw = prefilled?.passw ?: "",
                desc = prefilled?.desc ?: "",

                colorGroupVariants = colorGroups,
                colorGroupSelected = colorGroups
                    .indexOfFirst { prefilled?.colorGroupId == it.id }
                    .takeIf { it >= 0 } ?: 0,

                changeTime = prefilled?.chTime
                    ?.takeIf { it > 0L }
                    ?.let { dateFormat.format(Date(TimeUnit.SECONDS.toMillis(it))) }
                    ?: "",

                )
        }
    }

    fun input(block: EditNoteState.() -> EditNoteState) = scope.launchLatest("input") {
        var newState = block.invoke(state.value)
        if (!newState.isValid()) return@launchLatest

        val isSaveAvailable = when {
            originNote == null -> !newState.decryptedNote().isEmpty()
            originNote != null -> newState.decryptedNote(originNote!!) != originNote
            else -> false
        }
        newState = newState.copy(
            isSaveAvailable = isSaveAvailable,
            isRemoveAvailable = newState.isRemoveAvailable && !isSaveAvailable,
        )
        state.value = newState
    }

    fun showHistory() = scope.launchLatest("hist") {

    }

    fun tryRemove() = scope.launchLatest("rm") {
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
            interactor().removeNote(note.ptnote)
            router.back()
        }
    }

    fun scanQRCode() = scope.launchLatest("qr") {

    }

    fun save() = scope.launchLatest("safe") {
        val curState = state.value
        when (curState.page) {
            EditTabs.Account -> {
                val note = curState.decryptedNote(origin = originNote ?: DecryptedNote())
                if (note.isEmpty()) {
                    router.snack(R.string.note_is_empty)
                    return@launchLatest
                }

                val error = interactor().saveNote(note, setAll = true)
                router.back()
            }

            EditTabs.Otp -> {

            }
        }

    }

    fun generate() = scope.launchLatestSafe("gen") {
        val newPassw = interactor()
            .generateNewPassw(GenPasswParams(oldPassw = state.value.passw))
            .await()

        input { copy(passw = newPassw) }
    }


}