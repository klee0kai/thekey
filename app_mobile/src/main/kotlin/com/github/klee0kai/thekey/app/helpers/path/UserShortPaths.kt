package com.github.klee0kai.thekey.app.helpers.path

import android.os.Environment
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.utils.common.runForEach
import java.io.File
import java.util.Locale

class ShortPath(
    val short: String,
    longPath: String,
) {
    val absolutePath = longPath.fromRootPath()
    val shortFromRoot by lazy { short.fromRootPath() }
}

open class UserShortPaths {

    private val colorScheme by lazy { DI.theme().colorScheme() }

    open val shortPaths by lazy {
        listOf(
            ShortPath("appdata", DI.app().applicationInfo.dataDir),
            ShortPath(
                "phoneStorage",
                Environment.getExternalStorageDirectory().absolutePath
            ),
        )
    }

    val rootAbsolutePaths
        get() = shortPaths
            .map { it.absolutePath }
            .toTypedArray()

    val rootUserPaths
        get() = shortPaths
            .map { it.short }
            .toTypedArray()

    val colorTransformation
        get() = VisualTransformation { input ->
            TransformedText(
                text = buildAnnotatedString {
                    val coloredSpanStyle = SpanStyle(color = colorScheme.androidColorScheme.primary)
                    shortPaths.runForEach {
                        listOf(short, shortFromRoot, absolutePath).forEach { coloredPath ->
                            if (input.startsWith(coloredPath)) {
                                withStyle(coloredSpanStyle) { append(coloredPath) }
                                if (input.length > coloredPath.length) append(input.substring(coloredPath.length))
                                return@buildAnnotatedString
                            }
                        }
                    }
                    // without visualization
                    append(input)
                },
                offsetMapping = OffsetMapping.Identity,
            )
        }

    open fun shortPathName(originAbsolutePath: String): String {
        if (originAbsolutePath.isBlank()) {
            return originAbsolutePath.fromRootPath()
        }

        val path = runCatching { File(originAbsolutePath).absolutePath }
            .getOrNull()
            ?: originAbsolutePath

        shortPaths.runForEach {
            if (path.startsWith(absolutePath) || originAbsolutePath.startsWith(absolutePath)) {
                val pp = if (path.startsWith(absolutePath)) path else originAbsolutePath
                return (short + pp.substring(absolutePath.length)).fromRootPath()
            }
        }


        return path
    }

    open fun absolutePath(userShortPath: String?): String? {
        if (userShortPath.isNullOrBlank()) {
            return userShortPath
        }
        val lowerCase = userShortPath.lowercase(Locale.getDefault())
        shortPaths.runForEach {
            if (lowerCase.startsWith(short)) {
                return absolutePath + userShortPath.substring(short.length)
            }
            if (lowerCase.startsWith(short.fromRootPath())) {
                return absolutePath + userShortPath.substring(short.length + 1)
            }
        }
        return userShortPath.fromRootPath()
    }

}

fun String.fromRootPath(): String = when {
    isNullOrBlank() -> this
    firstOrNull() != '/' -> "/$this"
    else -> this
}