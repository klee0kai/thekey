package com.github.klee0kai.thekey.app.ui.storage.model

import android.os.Parcelable
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.domain.model.ColoredNote
import com.github.klee0kai.thekey.core.domain.model.ColoredOtpNote
import com.github.klee0kai.thekey.core.domain.model.dummyLoaded
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import kotlinx.parcelize.Parcelize
import kotlin.random.Random

@Parcelize
data class StorageItem(
    val note: ColoredNote? = null,
    val otp: ColoredOtpNote? = null,
) : Parcelable {
    companion object
}

val StorageItem.id get() = "${note?.ptnote}/${otp?.ptnote}"

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

@DebugOnly
fun StorageItem.Companion.dummy(): StorageItem {
    return if (Random.nextBoolean()) {
        StorageItem(note = ColoredNote.dummyLoaded())
    } else {
        StorageItem(otp = ColoredOtpNote.dummyLoaded())
    }
}