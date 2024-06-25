package com.github.klee0kai.thekey.app.ui.storage.model

import android.os.Parcelable
import com.github.klee0kai.thekey.core.domain.model.ColoredNote
import com.github.klee0kai.thekey.core.domain.model.ColoredOtpNote
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import kotlinx.parcelize.Parcelize

@Parcelize
data class StorageItem(
    val note: ColoredNote? = null,
    val otp: ColoredOtpNote? = null,
) : Parcelable

val StorageItem.group get() = note?.group ?: otp?.group ?: ColorGroup()

fun StorageItem.filterBy(filter: String): Boolean {
    return note?.site?.contains(filter, ignoreCase = true) ?: false
            || note?.login?.contains(filter, ignoreCase = true) ?: false
            || note?.desc?.contains(filter, ignoreCase = true) ?: false
            || otp?.issuer?.contains(filter, ignoreCase = true) ?: false
            || otp?.name?.contains(filter, ignoreCase = true) ?: false
}

fun StorageItem.sortableFlatText(): String {
    val site = note?.site ?: otp?.issuer ?: ""
    val login = note?.login ?: otp?.name ?: ""
    return "$site-$login"
}

fun ColoredNote.storageItem() = StorageItem(note = this)

fun ColoredOtpNote.storageItem() = StorageItem(otp = this)