package com.github.klee0kai.thekey.dynamic.findstorage.data.filesystem

import com.github.klee0kai.thekey.core.utils.common.appendPrefix
import com.github.klee0kai.thekey.core.utils.common.parents
import com.github.klee0kai.thekey.dynamic.findstorage.di.FSDI
import com.github.klee0kai.thekey.dynamic.findstorage.domain.model.FileItem
import com.github.klee0kai.thekey.dynamic.findstorage.domain.model.absParent
import java.io.File

open class FileSystemRepositoryDummy : FileSystemRepository {

    private val userShortPaths = FSDI.userShortPaths()
    private val files = buildList<FileItem> {

        userShortPaths.rootAbsolutePaths.forEach {
            File(it).parents.forEach { file ->
                add(fileItemFrom(file).copy(isFolder = true))
            }
        }

        add(fileItemFrom(File("/storage/emulated/0/Documents")).copy(isFolder = true))
        add(fileItemFrom(File("/storage/emulated/0/Downloads")).copy(isFolder = true))
        add(fileItemFrom(File("/storage/emulated/0/Downloads/some_folder")).copy(isFolder = true))
        add(fileItemFrom(File("/storage/emulated/0/Downloads/simple.txt")))
        add(fileItemFrom(File("/storage/emulated/0/Downloads/some_file.txt")))
        add(fileItemFrom(File("/storage/emulated/0/Icons")).copy(isFolder = true))
    }.toSet()
        .toList()

    override fun listFileItems(absFolderPath: String): List<FileItem> {
        val folderPath = absFolderPath.appendPrefix(File.separator)
        return when {
            folderPath == File.separator -> {
                userShortPaths.rootAbsolutePaths
                    .map { File(it) }
                    .map { fileItemFrom(it) }
            }

            else -> {
                files.filter { it.absParent == folderPath }
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