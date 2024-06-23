package com.github.klee0kai.thekey.app.ui.storagegroup.model

import android.os.Parcelable
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.domain.model.ColoredStorage
import kotlinx.parcelize.Parcelize


@Parcelize
data class SelectedStorage(
    val path: String = "",
    val name: String = "",
    val description: String = "",

    val group: ColorGroup? = null,
    val selected: Boolean = false,
) : Parcelable {
    companion object;
}

fun ColoredStorage.selected(selected: Boolean = false) = SelectedStorage(
    path = path,
    name = name,
    description = description,
    group = colorGroup,
    selected = selected,
)

fun SelectedStorage.toColorStorage() = ColoredStorage(
    path = path,
    name = name,
    description = description,
    colorGroup = group,
)

