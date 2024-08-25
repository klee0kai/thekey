package com.github.klee0kai.thekey.app.data.model

import android.os.Parcelable
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import kotlinx.parcelize.Parcelize


@Parcelize
data class AuthorizedStorage(
    val identifier: StorageIdentifier,
    val loginTime: Long = System.currentTimeMillis(),
) : Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AuthorizedStorage

        return identifier == other.identifier
    }

    override fun hashCode(): Int {
        return identifier.hashCode()
    }

}
