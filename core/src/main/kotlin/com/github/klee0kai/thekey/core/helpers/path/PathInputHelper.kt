package com.github.klee0kai.thekey.core.helpers.path

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import com.github.klee0kai.thekey.core.di.CoreDI
import com.github.klee0kai.thekey.core.utils.common.runForEach
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import java.io.FilenameFilter
import java.util.Locale

class PathInputHelper {

    private val shortPaths by lazy { CoreDI.userShortPaths() }
    private val dirFileNameFilter = FilenameFilter { dir, name -> File(dir, name).isDirectory }


    fun AnnotatedString.coloredPath(
        accentColor: Color = Color.Blue,
    ) = buildAnnotatedString {
        val input = this@coloredPath
        val coloredSpanStyle = SpanStyle(color = accentColor)
        shortPaths.shortPaths.runForEach {
            listOf(short, shortFromRoot, absolutePath).forEach { coloredPath ->
                if (input.startsWith("$coloredPath/")) {
                    withStyle(coloredSpanStyle) { append(coloredPath) }
                    if (input.length > coloredPath.length) append(input.substring(coloredPath.length))
                    return@buildAnnotatedString
                }
            }
        }
        // without visualization
        append(input)
    }

    fun TextFieldValue.pathInputMask(): TextFieldValue {
        val input = this
        val searchAbsPath = shortPaths.absolutePath(input.text) ?: ""
        val shortPath = buildString {
            val shortInput = shortPaths.shortPathName(searchAbsPath)
            append(shortInput)
            if (input.text.lastOrNull() == '/' && shortInput.lastOrNull() != '/') {
                append("/")
            }
        }
        return input.copy(
            annotatedString = AnnotatedString(shortPath),
            selection = if (input.text != shortPath) TextRange(shortPath.length) else input.selection,
        )
    }

    fun String.pathVariables() = flow<List<String>> {
        val input = this@pathVariables
        emit(emptyList())
        val searchAbsPath = shortPaths.absolutePath(input) ?: ""
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
        emit(availableList)
    }.flowOn(CoreDI.defaultDispatcher())

    fun String.folderSelected(selected: String): String {
        val input = this
        val isDir = input.lastOrNull() == '/'
        val parent = (if (isDir) File(input) else File(input).parentFile)
            ?.absolutePath
            ?: ""
        return File(parent, selected).absolutePath.fromRootPath() + "/"
    }

    fun String.shortPath() = shortPaths.shortPathName(this)

    fun String.absolutePath() = shortPaths.absolutePath(this)


}
