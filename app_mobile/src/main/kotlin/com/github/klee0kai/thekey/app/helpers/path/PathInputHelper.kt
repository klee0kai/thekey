package com.github.klee0kai.thekey.app.helpers.path

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
        emit(PathSearchResult(textField = input.fromRootPath()))

        val searchAbsPath = shortPaths.absolutePath(input.text) ?: ""
        val isDir = searchAbsPath.lastOrNull() == '/'
        val searchFileName = if (!isDir) {
            File(searchAbsPath).name.lowercase(Locale.getDefault())
        } else {
            ""
        }
        val parent = if (isDir) File(searchAbsPath) else File(searchAbsPath).parentFile

        val availableList = if (parent != null && parent.absolutePath != "/") {
            parent.list(dirFileNameFilter) ?: emptyArray()
        } else {
            shortPaths.rootUserPaths
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
                    selection = TextRange(shortPath.length),
                ),
                variants = availableList.map { AnnotatedString(it) }
            )
        )

    }.flowOn(DI.defaultDispatcher())
        .flowOn(DI.mainDispatcher())


    fun folderSelected(input: String, selected: String): String {
        val isDir = input.lastOrNull() == '/'
        val parent = (if (isDir) File(input) else File(input).parentFile)
            ?.path
            ?: ""
        return ("$parent/$selected/").fromRootPath()
    }


}


fun TextFieldValue.fromRootPath(): TextFieldValue = when {
    text.isEmpty() -> this
    text.firstOrNull() != '/' ->
        copy(
            text = "/${text}",
            selection = TextRange(selection.end + 1),
        )

    else -> this


}