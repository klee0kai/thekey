package com.github.klee0kai.thekey.core.helpers.path

import android.os.Environment
import com.github.klee0kai.thekey.core.di.CoreDI
import com.github.klee0kai.thekey.core.utils.common.runForEach
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

    open val appPath: String by lazy { CoreDI.ctx().applicationInfo.dataDir + "/storages" }

    open val shortPaths by lazy {
        listOf(
            ShortPath("appdata", appPath),
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

    open fun isExternal(path: String): Boolean {
        return !path.startsWith(appPath)
    }

    open fun isAppInner(path: String): Boolean {
        return path.startsWith(appPath)
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
            val shortLower = short.lowercase(Locale.getDefault())
            if (lowerCase.startsWith(shortLower)) {
                return absolutePath + userShortPath.substring(short.length)
            }
            if (lowerCase.startsWith(shortLower.fromRootPath())) {
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