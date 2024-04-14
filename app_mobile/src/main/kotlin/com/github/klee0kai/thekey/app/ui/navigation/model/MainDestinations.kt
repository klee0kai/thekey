package com.github.klee0kai.thekey.app.ui.navigation.model

import android.os.Parcelable
import androidx.compose.runtime.Stable
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.ui.note.model.EditTabs
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
data class EditNoteDestination(
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

    /**
     * opened tab
     */
    val tab: EditTabs = EditTabs.Account,
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


@Parcelize
data class EditNoteGroupDestination(
    /**
     * storage identifier
     */
    val storageIdentifier: StorageIdentifier = StorageIdentifier(),
    /**
     * group id
     */
    val groupId: Long? = null,
) : Destination


@Parcelize
data object QRCodeScanDestination : Destination

