package com.github.klee0kai.thekey.core.di.identifiers

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NoteGroupIdentifier(
    val storageIdentifier: StorageIdentifier,
    val groupId: Long? = null,
) : Parcelable
