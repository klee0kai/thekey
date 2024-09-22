package com.github.klee0kai.thekey.dynamic.findstorage.ui.editstorage.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.File

@Parcelize
data class FileItem(
    val path: String = "",
    val isFolder: Boolean = false,
) : Parcelable


fun File.toFileItem(
) = FileItem(
    path = absolutePath,
    isFolder = isDirectory,
)