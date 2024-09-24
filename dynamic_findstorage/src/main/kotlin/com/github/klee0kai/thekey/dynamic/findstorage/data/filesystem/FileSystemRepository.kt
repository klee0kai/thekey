package com.github.klee0kai.thekey.dynamic.findstorage.data.filesystem

import com.github.klee0kai.thekey.dynamic.findstorage.domain.model.FileItem
import java.io.File

interface FileSystemRepository {

    fun listFileItems(absFolderPath: String = ""): List<FileItem> = emptyList()

    fun fileItemFrom(file: File) = FileItem()


}