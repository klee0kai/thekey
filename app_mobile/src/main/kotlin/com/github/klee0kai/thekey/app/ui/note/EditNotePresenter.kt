package com.github.klee0kai.thekey.app.ui.note

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.NoteIdentifier
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.engine.model.colorGroup
import com.github.klee0kai.thekey.app.engine.model.isEmpty
import com.github.klee0kai.thekey.app.model.ColorGroup
import com.github.klee0kai.thekey.app.model.noGroup
import com.github.klee0kai.thekey.app.ui.navigation.storage
import com.github.klee0kai.thekey.app.ui.note.model.EditNoteState
import com.github.klee0kai.thekey.app.ui.note.model.decryptedNote
import com.github.klee0kai.thekey.app.ui.note.model.isValid
import com.github.klee0kai.thekey.app.utils.common.launchLatest
import com.github.klee0kai.thekey.app.utils.common.launchLatestSafe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditNotePresenter(
    val identifier: NoteIdentifier,
) {
    private val otpTypesVariants = listOf("HOTP", "TOTP", "YaOTP")
    private val otpAlgoVariants = listOf("SHA1", "SHA256", "SHA512")

    private val scope = DI.defaultThreadScope()
    private val navigator = DI.router()
    private val engine = DI.cryptStorageEngineSafeLazy(identifier.storage())

    private var originNote: DecryptedNote? = null
    private var colorGroups: List<ColorGroup> = emptyList()
    val state = MutableStateFlow(EditNoteState(isSkeleton = true))

    fun init(prefilled: DecryptedNote? = null) = scope.launch {
        val isEditMode = identifier.notePtr != 0L
        val initState = EditNoteState(
            isEditMode = isEditMode,
            isSkeleton = true,
            isRemoveAvailable = isEditMode,
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
            originNote = engine()?.note(identifier.notePtr) ?: DecryptedNote()
            prefilled = originNote
        }
        colorGroups = (engine()?.colorGroups(true)
            ?.map { it.colorGroup() } ?: emptyList())
            .let { listOf(ColorGroup.noGroup()) + it }


        prefilled?.chTime
            ?.takeIf { it > 0L }
            ?.let {

            }


        state.update { note ->
            note.copy(
                isSkeleton = false,
                siteOrIssuer = prefilled?.site ?: "",
                login = prefilled?.login ?: "",
                passw = prefilled?.passw ?: "",
                desc = prefilled?.desc ?: "",

                colorGroupVariants = colorGroups,
                colorGroupSelected = colorGroups
                    .indexOfFirst { prefilled?.colorGroupId == it.id }
                    .takeIf { it >= 0 } ?: 0,


//                chTime = prefilled?.chTime,
//                colorGroupId = prefilled?.colorGroupId,
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


    fun save() = scope.launchLatest("safe") {
//        val note = note.value
//        if (note.isEmpty()) {
//            navigator.snack(R.string.note_is_empty)
//            return@launchLatest
//        }
//
//        val error = engine()?.saveNote(note, setAll = true)
//        navigator.back()
    }

    fun generate() = scope.launchLatestSafe("gen") {
//        val newPassw = engine()?.generateNewPassw(GenPasswParams(oldPassw = note.value.passw))
//            ?: return@launchLatestSafe
//
//        note.value = note.value.copy(passw = newPassw)
    }


}