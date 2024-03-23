package com.github.klee0kai.thekey.app.ui.navigation.model

import android.os.Parcelable
import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize

@Stable
interface Destination : Parcelable

@Parcelize
data object LoginDestination : Destination

@Parcelize
data object StoragesDestination : Destination

@Parcelize
data class EditStorageDestination(
    /**
     * storage path
     */
    val path: String = ""
) : Destination

@Parcelize
data object DesignDestination : Destination

@Parcelize
data class StorageDestination(
    /**
     * storage path
     */
    val path: String = "",
) : Destination

@Parcelize
data class NoteDestination(
    /**
     * storage path
     */
    val path: String = "",
    /**
     * Note mem ptr
     */
    val notePtr: Long = 0,
) : Destination
