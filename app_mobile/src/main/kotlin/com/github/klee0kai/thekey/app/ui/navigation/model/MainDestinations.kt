package com.github.klee0kai.thekey.app.ui.navigation.model

import android.os.Parcelable
import androidx.compose.runtime.Stable
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
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

    /**
     * Storage version
     */
    val version: Int = 0,

    /**
     * selected page
     */
    val selectedPage: Int = 0,
) : Destination

@Parcelize
data class NoteDestination(
    /**
     * storage path
     */
    val path: String = "",
    /**
     * Storage version
     */
    val storageVersion: Int = 0,
    /**
     * Note mem ptr
     */
    val notePtr: Long = 0,

    /**
     * prefilled note
     */
    val prefilled: DecryptedNote? = null,
) : Destination


@Parcelize
data class GenHistDestination(
    /**
     * storage path
     */
    val path: String = "",
    /**
     * Storage version
     */
    val storageVersion: Int = 0,
) : Destination

