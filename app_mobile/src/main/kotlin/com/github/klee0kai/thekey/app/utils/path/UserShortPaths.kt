package com.github.klee0kai.thekey.app.utils.path

import android.os.Environment
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.utils.common.runForEach
import java.io.File
import java.util.Locale

class ShortPath(
    val short: String,
    longPath: String,
) {
    val absolutePath = longPath.fromRootPath()
}

open class UserShortPaths {

    open val appData by lazy {
        ShortPath("appdata", DI.app().applicationInfo.dataDir)
    }

    open val phoneStorage by lazy {
        ShortPath(
            "phoneStorage",
            Environment.getExternalStorageDirectory().absolutePath
        )
    }

    val rootAbsolutePaths
        get() = listOf(appData, phoneStorage)
            .map { it.absolutePath }
            .toTypedArray()

    val rootUserPaths
        get() = listOf(appData, phoneStorage)
            .map { it.short }
            .toTypedArray()

    val colorTransformation
        get() = VisualTransformation {
            TransformedText(
                text = buildAnnotatedString {

                },
                offsetMapping = OffsetMapping.Identity,
            )
        }

    open fun shortPathName(originAbsolutePath: String): String {
        if (originAbsolutePath.isBlank()) {
            return originAbsolutePath.fromRootPath()
        }

        val path = runCatching { File(originAbsolutePath).canonicalPath }
            .getOrNull()
            ?: originAbsolutePath

        listOf(appData, phoneStorage).runForEach {
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
        listOf(appData, phoneStorage).runForEach {
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