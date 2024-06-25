package com.github.klee0kai.thekey.core.ui.navigation.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import com.github.klee0kai.thekey.core.domain.model.ColoredStorage

@Stable
interface WidgetState

data class StoragesListWidgetState(
    val isExtStorageSelected: Boolean = false,
    val isShowStoragesTitle: Boolean = false,
) : WidgetState

data class StoragesButtonsWidgetState(
    val isExtStorageSelected: Boolean = false,
) : WidgetState

data class StoragesStatusBarWidgetState(
    val isContentExpanded: Boolean? = null,
) : WidgetState

data class StorageItemWidgetState(
    val coloredStorage: ColoredStorage = ColoredStorage(),
    val isPopupMenuAvailable: Boolean = false,
    val onClick: () -> Unit = {},
    val iconContent: (@Composable () -> Unit)? = null,
) : WidgetState

