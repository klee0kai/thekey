package com.github.klee0kai.thekey.app.utils.path

import android.os.Environment
import android.text.Spannable
import android.text.SpannableString
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.utils.common.runForEach
import java.io.File
import java.util.Locale

data class ShortPath(
    val short: String,
    val absolutePath: String,
)

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

    open fun shortPathName(p: String): SpannableString {
        val path = runCatching { File(p).canonicalPath }.getOrNull() ?: p
        val colorScheme = DI.theme().colorScheme().androidColorScheme
        var userPath: SpannableString? = null

        listOf(appData, phoneStorage).runForEach {
            if (path.startsWith(absolutePath) || p.startsWith(absolutePath)) {
                val pp = if (path.startsWith(absolutePath)) path else p
                userPath = SpannableString(short + pp.substring(absolutePath.length))
                userPath!!.setSpan(
                    colorScheme.primary,
                    0,
                    short.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                return userPath!!
            }
        }


        return SpannableString(path)
    }

    open fun absolutePath(userShortPath: String?): String? {
        val lowerCase = userShortPath?.lowercase(Locale.getDefault()) ?: return null
        listOf(appData, phoneStorage).runForEach {
            if (lowerCase.startsWith(short)) {
                return absolutePath + userShortPath.substring(short.length)
            }
        }
        return userShortPath
    }


}