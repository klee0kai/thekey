package com.github.klee0kai.thekey.dynamic.findstorage.data.filesystem

import com.github.klee0kai.thekey.dynamic.findstorage.di.FSDI
import com.github.klee0kai.thekey.dynamic.findstorage.domain.model.FileItem
import java.io.File

class FileSystemRepositoryImpl : FileSystemRepository {

    private val userShortPaths = FSDI.userShortPaths()

    override fun listFileItems(absFolderPath: String): List<FileItem> {
        return when {
            absFolderPath == File.separator
                    || userShortPaths.rootAbsolutePaths.any { it.startsWith(absFolderPath) } -> {
                userShortPaths.rootAbsolutePaths
                    .filter { it.startsWith(absFolderPath) }
                    .map { File(it) }
                    .map { fileItemFrom(it) }
            }

            else -> {
                File(absFolderPath)
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