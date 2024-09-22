package com.github.klee0kai.thekey.dynamic.findstorage.data.filesystem

import com.github.klee0kai.thekey.core.utils.common.appendPrefix
import com.github.klee0kai.thekey.dynamic.findstorage.di.FSDI
import com.github.klee0kai.thekey.dynamic.findstorage.domain.model.FileItem
import java.io.File

class FileSystemRepositoryImpl : FileSystemRepository {

    private val userShortPaths = FSDI.userShortPaths()

    override fun listFileItems(absFolderPath: String): List<FileItem> {
        val folderPath = absFolderPath.appendPrefix(File.separator)
        return when {
            folderPath == File.separator -> {
                userShortPaths.rootAbsolutePaths
                    .map { File(it) }
                    .map { fileItemFrom(it) }
            }

            else -> {
                File(folderPath)
                    .listFiles()
                    ?.toList()
                    ?.map { fileItemFrom(it) }
                    ?: emptyList()
            }
        }
    }


    override fun fileItemFrom(
        file: File,
    ) = FileItem(
        absPath = file.absolutePath,
        userPath = userShortPaths.shortPathName(file.absolutePath),
        isFolder = file.isDirectory,
        isAppInner = userShortPaths.isAppInner(file.absolutePath),
        isExternal = userShortPaths.isExternal(file.absolutePath),
    )


}