package com.github.klee0kai.thekey.app.ui.storage.model

import android.os.Parcelable
import com.github.klee0kai.thekey.core.domain.basemodel.BaseModel
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
) : Parcelable, BaseModel<String> {
    companion object;

    override val id: String get() = "${note?.id}/${otp?.id}"

    override val isLoaded: Boolean get() = note?.isLoaded ?: otp?.isLoaded ?: false

    override fun filterBy(filter: String): Boolean {
        return note?.filterBy(filter) ?: otp?.filterBy(filter) ?: false
    }

    override fun sortableFlatText(): String {
        return note?.sortableFlatText() ?: otp?.sortableFlatText() ?: ""
    }

}

val StorageItem.group get() = note?.group ?: otp?.group ?: ColorGroup()

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