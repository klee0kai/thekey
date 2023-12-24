package com.github.klee0kai.thekey.app.ui.navigation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

interface Destination : Parcelable


@Parcelize
data object LoginDestination : Destination

@Parcelize
data object StoragesDestination : Destination

@Parcelize
data object DesignDestination : Destination

@Parcelize
data class StorageDestination(
    /**
     * storage path
     */
    val path: String,
) : Destination

@Parcelize
data class NoteDestination(
    /**
     * Note mem ptr
     */
    val notePtr: Long = 0,
) : Destination
