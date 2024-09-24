package com.github.klee0kai.thekey.dynamic.findstorage.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.File

@Parcelize
data class FileItem(
    /**
     * full file path
     */
    val absPath: String = "",
    /**
     * user Short path
     */
    val userPath: String = "",

    /**
     * is folder
     */
    val isFolder: Boolean = false,

    /**
     * this file in app's folder
     */
    val isAppInner: Boolean = false,

    /**
     * this file in external folder
     */
    val isExternal: Boolean = false,
) : Parcelable {

    override fun toString(): String {
        return "FileItem(absPath=\"$absPath\", userPath=\"$userPath\", isFolder=$isFolder, isAppInner=$isAppInner, isExternal=$isExternal)"
    }

}

val FileItem.absName: String
    get() {
        val index = absPath.lastIndexOf(File.separatorChar)
        return absPath.substring(index + 1)
    }

val FileItem.userName: String
    get() {
        val index = userPath.lastIndexOf(File.separatorChar)
        return userPath.substring(index + 1)
    }


val FileItem.absParent: String?
    get() {
        val index = absPath.lastIndexOf(File.separatorChar)
        if (index == 0) return null
        return absPath.substring(0, index)
    }

val FileItem.absCurOrParent: String?
    get() {
        val index = absPath.lastIndexOf(File.separatorChar)
        if (index == 0) return null
        return absPath.substring(0, index + 1)
    }


val FileItem.userParent: String?
    get() {
        val index = userPath.lastIndexOf(File.separatorChar)
        if (index == 0) return null
        return userPath.substring(0, index)
    }

val FileItem.userCurOrParent: String?
    get() {
        val index = userPath.lastIndexOf(File.separatorChar)
        if (index == 0) return null
        return userPath.substring(0, index + 1)
    }


@Deprecated("not support")
fun File.toFileItem(
) = FileItem(
    absPath = absolutePath,
    isFolder = isDirectory,
)