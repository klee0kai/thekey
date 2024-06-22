package com.github.klee0kai.thekey.core.ui.navigation.model

import android.os.Parcelable
import androidx.compose.runtime.Stable
import com.github.klee0kai.thekey.core.domain.model.ColoredStorage
import kotlinx.parcelize.Parcelize

@Stable
interface WidgetState : Parcelable

@Parcelize
data class StoragesListWidgetState(
    val isExtStorageSelected: Boolean = false,
    val isShowStoragesTitle: Boolean = false,
) : WidgetState

@Parcelize
data class StoragesButtonsWidgetState(
    val isExtStorageSelected: Boolean = false,
) : WidgetState

@Parcelize
data class StoragesStatusBarWidgetState(
    val isContentExpanded: Boolean? = null,
) : WidgetState

@Parcelize
data class StorageItemWidgetState(
    val coloredStorage: ColoredStorage = ColoredStorage(),
    val isPopupMenuAvailable: Boolean = false,
) : WidgetState

