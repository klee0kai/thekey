package com.github.klee0kai.thekey.dynamic.findstorage.domain

import com.github.klee0kai.thekey.dynamic.findstorage.di.FSDI
import com.github.klee0kai.thekey.dynamic.findstorage.domain.model.absName
import kotlinx.coroutines.async
import java.io.File
import java.util.Locale

class FileSystemInteractor {

    private val scope = FSDI.defaultThreadScope()
    private val repository = FSDI.fsFileSystemRepositoryLazy()
    private val locale = Locale.getDefault()
    private val userShortPaths = FSDI.userShortPaths()

    fun listFiles(
        searchPath: String = "",
    ) = scope.async {
        val searchAbsPath = userShortPaths.absolutePath(searchPath) ?: ""
        val isDir = searchAbsPath.lastOrNull() == '/'
        val absSearchFile = File(searchAbsPath)
        val parent: File? = if (isDir) absSearchFile else absSearchFile.parentFile

        val allAvailableFiles = repository().listFileItems(parent?.absolutePath ?: "")

        if (!isDir) {
            val filterName = absSearchFile.name.lowercase(locale)
            allAvailableFiles.filter { file ->
                file.absName.lowercase(locale).contains(filterName)
                        || file.userPath.lowercase(locale).contains(filterName)
            }
        } else {
            allAvailableFiles
        }
    }

    fun fileItemFrom(
        file: File,
    ) = scope.async {
        repository().fileItemFrom(file)
    }

}