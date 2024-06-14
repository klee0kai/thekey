package com.github.klee0kai.thekey.app.ui.storagegroup.model

import android.os.Parcelable
import com.github.klee0kai.thekey.app.domain.model.ColoredStorage
import com.github.klee0kai.thekey.core.domain.ColorGroup
import com.github.klee0kai.thekey.core.domain.noGroup
import kotlinx.parcelize.Parcelize


@Parcelize
data class SelectedStorage(
    val path: String = "",
    val name: String = "",
    val description: String = "",

    val group: ColorGroup = ColorGroup.noGroup(),
    val selected: Boolean = false,
) : Parcelable {
    companion object;
}

fun ColoredStorage.selected(selected: Boolean = false) = SelectedStorage(
    path = path,
    name = name,
    description = description,
    group = colorGroup ?: ColorGroup.noGroup(),
    selected = selected,
)
