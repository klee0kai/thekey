package com.github.klee0kai.thekey.app.utils.path

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.github.klee0kai.thekey.app.di.DI
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import java.io.FilenameFilter
import java.util.Locale

class PathSearchResult(
    val textField: TextFieldValue = TextFieldValue(""),
    val variants: List<AnnotatedString> = emptyList(),
)

class PathInputHelper {

    private val shortPaths = DI.userShortPaths()
    private val dirFileNameFilter = FilenameFilter { dir, name -> File(dir, name).isDirectory }

    fun autoCompleteVariants(input: TextFieldValue) = flow<PathSearchResult> {
        if (input.text.isEmpty()) {
            val roots = shortPaths.rootUserPaths.asList()
            emit(
                PathSearchResult(
                    textField = input.copy(),
                    variants = roots.map { AnnotatedString(it) }
                )
            )
            return@flow
        }
        emit(PathSearchResult(textField = input.copy()))

        val searchAbsPath = DI.userShortPaths().absolutePath(input.text) ?: ""
        val isDir = searchAbsPath.lastOrNull() == '/'
        val searchFileName = if (!isDir) {
            File(searchAbsPath).name.lowercase(Locale.getDefault())
        } else {
            ""
        }
        val parent = if (isDir) File(searchAbsPath) else File(searchAbsPath).parentFile

        val availableList = if (parent != null) {
            parent.list(dirFileNameFilter) ?: emptyArray()
        } else {
            shortPaths.rootAbsolutePaths
        }.filter { it.lowercase(Locale.getDefault()).contains(searchFileName) }

        val shortPath = buildString {
            val shortInput = shortPaths.shortPathName(searchAbsPath)
            append(shortInput)
            if (input.text.lastOrNull() == '/' && shortInput.lastOrNull() != '/') {
                append("/")
            }
        }
        emit(
            PathSearchResult(
                textField = input.copy(
                    annotatedString = AnnotatedString(shortPath),
                    TextRange(shortPath.length),
                ),
                variants = availableList.map { AnnotatedString(it) }
            )
        )

    }.flowOn(DI.defaultDispatcher())
        .flowOn(DI.mainDispatcher())


}