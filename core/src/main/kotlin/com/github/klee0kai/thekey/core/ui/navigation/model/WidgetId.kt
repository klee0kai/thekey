package com.github.klee0kai.thekey.core.ui.navigation.model

import android.os.Parcelable
import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize

@Stable
interface WidgetId : Parcelable

@Parcelize
data class StoragesListWidgetId(
    val isExtStorageSelected: Boolean = false,
    val isShowStoragesTitle: Boolean = false,
) : WidgetId

@Parcelize
data class StoragesButtonsWidgetId(
    val isExtStorageSelected: Boolean = false,
) : WidgetId

