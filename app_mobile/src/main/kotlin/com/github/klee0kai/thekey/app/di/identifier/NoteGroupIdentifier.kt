package com.github.klee0kai.thekey.app.di.identifier

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NoteGroupIdentifier(
    val storageIdentifier: StorageIdentifier,
    val groupId: Long? = null,
) : Parcelable
