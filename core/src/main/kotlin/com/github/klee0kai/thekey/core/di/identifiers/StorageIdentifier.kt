package com.github.klee0kai.thekey.core.di.identifiers

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StorageIdentifier(
    val path: String = "",
    val version: Int = 0,
    val fileDescriptor: Int? = null,
    val openReason: String? = null,
) : Parcelable {

    val engineIdentifier get() = "${path}-${openReason}"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as StorageIdentifier
        if (path != other.path) return false
        if (openReason != other.openReason) return false

        // compare file descriptor only if both exists
        if (fileDescriptor != null
            && other.fileDescriptor != null
            && fileDescriptor != other.fileDescriptor
        ) return false

        // compare version only if both exists
        if (version != 0 && other.version != 0 && version != other.version) return false
        return true
    }

    override fun hashCode(): Int {
        return path.hashCode()
    }
}
