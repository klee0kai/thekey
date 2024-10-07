package com.github.klee0kai.thekey.app.ui.navigation.model

import com.github.klee0kai.thekey.core.ui.navigation.model.DialogDestination
import kotlinx.parcelize.Parcelize

@Parcelize
data object SelectStorageDialogDestination : DialogDestination

@Parcelize
data class NoteDialogDestination(
    /**
     * storage path
     */
    val path: String = "",
    /**
     * Storage version
     */
    val storageVersion: Int = 0,

    /**
     * note id
     */
    val notePtr: Long? = null,

    /**
     * otp note id
     */
    val otpNotePtr: Long? = null,
) : DialogDestination

@Parcelize
data object DebugFlagsDialogDestination : DialogDestination
