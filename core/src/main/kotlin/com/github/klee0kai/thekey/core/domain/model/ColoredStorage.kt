package com.github.klee0kai.thekey.core.domain.model

import android.os.Parcelable
import com.github.klee0kai.thekey.core.domain.basemodel.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class ColoredStorage(
    val path: String = "",
    val name: String = "",
    val description: String = "",
    val version: Int = 0,
    val colorGroup: ColorGroup? = null
) : Parcelable, BaseModel<String> {
    companion object;

    override val id: String get() = path

    override fun filterBy(filter: String): Boolean {
        return path.contains(filter, ignoreCase = true)
                || name.contains(filter, ignoreCase = true)
    }

    override fun sortableFlatText(): String {
        return "${path}-${name}-${description}"
    }

    override val isLoaded: Boolean get() = true

}

fun ColoredStorage.isValid() = path.isNotBlank() && version > 0