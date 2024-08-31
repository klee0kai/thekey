package com.github.klee0kai.thekey.app.ui.noteedit.model

import android.os.Parcelable
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.engine.model.DecryptedOtpNote
import com.github.klee0kai.thekey.app.engine.model.OtpAlgo
import com.github.klee0kai.thekey.app.engine.model.OtpMethod
import com.github.klee0kai.thekey.app.ui.navigation.model.EditNoteDestination
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import kotlinx.parcelize.Parcelize
import java.text.DateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

@Parcelize
data class EditNoteState(
    val isEditMode: Boolean = false,
    val isSkeleton: Boolean = false,
    val page: EditTabs = EditTabs.Account,
    val isSaveAvailable: Boolean = false,
    val isRemoveAvailable: Boolean = false,

    val siteOrIssuer: String = "",
    val loginOrName: String = "",
    val passw: String = "",
    val desc: String = "",

    val otpUrl: String = "",
    val otpSecret: String = "",
    val otpMethodExpanded: Boolean = false,
    val otpMethodVariants: List<String> = emptyList(),
    val otpMethodSelected: Int = 0,
    val otpAlgoExpanded: Boolean = false,
    val otpAlgoVariants: List<String> = emptyList(),
    val otpAlgoSelected: Int = 0,
    val otpInterval: String = "",
    val otpDigits: String = "",
    val otpCounter: String = "",

    val colorGroupExpanded: Boolean = false,
    val colorGroupSelected: Int = 0,
    val colorGroupVariants: List<ColorGroup> = emptyList(),

    val changeTime: String = "",
) : Parcelable {

    companion object {
        val otpTypesVariants = listOf("HOTP", "TOTP", "YaOTP")
        val otpAlgoVariants = listOf("SHA1", "SHA256", "SHA512")

        fun OtpMethod.typeVariant() = when (this) {
            OtpMethod.OTP, OtpMethod.HOTP -> 0
            OtpMethod.TOTP -> 1
            OtpMethod.YAOTP -> 2
        }

        fun Int.methodVariantToOtpMethod() = when (this) {
            0 -> OtpMethod.HOTP
            1 -> OtpMethod.TOTP
            2 -> OtpMethod.YAOTP
            else -> OtpMethod.OTP
        }

        fun OtpAlgo.algoVariant() = when (this) {
            OtpAlgo.SHA1 -> 0
            OtpAlgo.SHA256 -> 1
            OtpAlgo.SHA512 -> 2
        }

        fun Int.algoVariantToOtpAlgo() = when (this) {
            0 -> OtpAlgo.SHA1
            1 -> OtpAlgo.SHA256
            2 -> OtpAlgo.SHA512
            else -> OtpAlgo.SHA1
        }
    }
}

enum class EditTabs {
    Account,
    Otp,
}

fun EditNoteState.isValid(): Boolean {
    val allDigits = otpInterval.all { it.isDigit() } && otpDigits.all { it.isDigit() } && otpCounter.all { it.isDigit() }
    val correctLen = otpInterval.length <= 4 && otpDigits.length <= 2
    return allDigits && correctLen
}

fun EditNoteState.decryptedNote(origin: DecryptedNote = DecryptedNote()) =
    origin.copy(
        site = siteOrIssuer,
        login = loginOrName,
        passw = passw,
        desc = desc,
        colorGroupId = colorGroupVariants.getOrNull(colorGroupSelected)?.id ?: 0L
    )

fun EditNoteState.decryptedOtpNote(origin: DecryptedOtpNote = DecryptedOtpNote()) = with(EditNoteState.Companion) {
    origin.copy(
        issuer = siteOrIssuer,
        name = loginOrName,
        url = otpUrl,
        secret = otpSecret,
        otpMethodRaw = otpMethodSelected.methodVariantToOtpMethod().code,
        otpAlgoRaw = otpAlgoSelected.algoVariantToOtpAlgo().code,
        interval = otpInterval.toIntOrNull() ?: 0,
        digits = otpDigits.toIntOrNull() ?: 0,
        counter = otpCounter.toIntOrNull() ?: 0,
    )
}

fun EditNoteState.updateWith(
    note: DecryptedNote,
    colorGroups: List<ColorGroup> = emptyList(),
    dateFormat: DateFormat? = null,
): EditNoteState {
    var state = copy(
        siteOrIssuer = note.site,
        loginOrName = note.login,
        passw = note.passw,
        desc = note.desc,
        colorGroupVariants = colorGroups,
        colorGroupSelected = colorGroups
            .indexOfFirst { note.colorGroupId == it.id }
            .takeIf { it >= 0 } ?: 0,
    )
    if (dateFormat != null && note.chTime > 0L) {
        state = state.copy(
            changeTime = dateFormat.format(Date(TimeUnit.SECONDS.toMillis(note.chTime)))
        )
    }
    return state
}

fun EditNoteState.updateWith(otp: DecryptedOtpNote): EditNoteState = with(EditNoteState.Companion) {
    copy(
        siteOrIssuer = otp.issuer,
        loginOrName = otp.name,
        otpUrl = otp.url,
        otpSecret = otp.secret,
        otpMethodSelected = otp.otpMethod.typeVariant(),
        otpAlgoSelected = otp.otpAlgo.algoVariant(),
        otpInterval = otp.interval.toString(),
        otpDigits = otp.digits.toString(),
        otpCounter = otp.counter.toString(),
    )
}

fun EditNoteDestination.initTab() = when {
    note != null && note.ptnote != 0L -> EditTabs.Account
    otpNote != null && otpNote.ptnote != 0L -> EditTabs.Otp
    else -> tab
}