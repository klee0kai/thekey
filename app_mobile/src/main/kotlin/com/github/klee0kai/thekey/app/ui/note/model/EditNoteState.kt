package com.github.klee0kai.thekey.app.ui.note.model

import android.os.Parcelable
import com.github.klee0kai.thekey.app.domain.model.ColorGroup
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import kotlinx.parcelize.Parcelize

@Parcelize
data class EditNoteState(
    val isEditMode: Boolean = false,
    val isSkeleton: Boolean = false,
    val page: EditTabs = EditTabs.Account,
    val isSaveAvailable: Boolean = false,
    val isRemoveAvailable: Boolean = false,

    val siteOrIssuer: String = "",
    val login: String = "",
    val passw: String = "",
    val desc: String = "",

    val otpSecret: String = "",
    val otpTypeExpanded: Boolean = false,
    val otpTypeVariants: List<String> = emptyList(),
    val otpTypeSelected: Int = 0,
    val otpAlgoExpanded: Boolean = false,
    val otpAlgoVariants: List<String> = emptyList(),
    val otpAlgoSelected: Int = 0,
    val otpPeriod: String = "",
    val otpDigits: String = "",
    val otpCounter: String = "",

    val colorGroupExpanded: Boolean = false,
    val colorGroupSelected: Int = 0,
    val colorGroupVariants: List<ColorGroup> = emptyList(),

    val changeTime: String = "",
) : Parcelable

enum class EditTabs {
    Account,
    Otp,
}

fun EditNoteState.isValid(): Boolean {
    val allDigits = otpPeriod.all { it.isDigit() } && otpDigits.all { it.isDigit() } && otpCounter.all { it.isDigit() }
    val correctLen = otpPeriod.length <= 4 && otpDigits.length <= 2
    return allDigits && correctLen
}

fun EditNoteState.decryptedNote(origin: DecryptedNote = DecryptedNote()) =
    origin.copy(
        site = siteOrIssuer,
        login = login,
        passw = passw,
        desc = desc,
        colorGroupId = colorGroupVariants.getOrNull(colorGroupSelected)?.id ?: 0L
    )


