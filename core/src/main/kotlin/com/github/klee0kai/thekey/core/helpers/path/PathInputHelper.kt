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
import java.io.File

class PathInputHelper {

    private val shortPaths by lazy { CoreDI.userShortPaths() }

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

    fun AnnotatedString.coloredFileExt(
        extensionColor: Color = Color.Gray,
    ) = buildAnnotatedString {
        val input = this@coloredFileExt
        val coloredSpanStyle = SpanStyle(color = extensionColor)
        val extLen = File(input.text).extension.length
        if (extLen <= 0) {
            append(input)
        } else {
            append(input.subSequence(0..<input.length - extLen))
            withStyle(coloredSpanStyle) {
                append(input.substring(input.length - extLen..<input.length))
            }
        }
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

    fun String.shortPath() = shortPaths.shortPathName(this)

    fun String.absolutePath() = shortPaths.absolutePath(this)


}
