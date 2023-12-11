package com.github.klee0kai.thekey.app.utils.android

import android.content.Context
import android.os.Environment
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.utils.views.EmptyTextWatcher
import java.io.File
import java.util.Locale

object UserShortPaths {

    private const val APPDATA = "appdata"
    private const val PHONE_STORAGE = "phoneStorage"


    fun shortPathName(p: String): SpannableString {
        val path = runCatching { File(p).canonicalPath }.getOrNull() ?: p
        val context = DI.app()
        val colorScheme = DI.theme().colorScheme().androidColorScheme
        val appData = context.applicationInfo.dataDir
        val phoneStorage = Environment.getExternalStorageDirectory().absolutePath
        var userPath: SpannableString? = null
        if (path.startsWith(appData) || p.startsWith(appData)) {
            val pp = if (path.startsWith(appData)) path else p
            userPath = SpannableString(APPDATA + pp.substring(appData.length))
            userPath.setSpan(
                colorScheme.primary,
                0,
                APPDATA.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            return userPath
        }

        if (path.startsWith(phoneStorage) || p.startsWith(phoneStorage)) {
            val pp = if (path.startsWith(phoneStorage)) path else p
            userPath = SpannableString(PHONE_STORAGE + pp.substring(phoneStorage.length))
            userPath.setSpan(
                colorScheme.primary,
                0,
                PHONE_STORAGE.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            return userPath
        }

        return SpannableString(path)
    }

    fun absolutePath(userShortPath: String?): String? {
        val lowerCase = userShortPath?.lowercase(Locale.getDefault()) ?: return null
        if (lowerCase.startsWith(APPDATA)) {
            return DI.app().applicationInfo.dataDir + userShortPath.substring(APPDATA.length)
        }
        return if (lowerCase.startsWith(PHONE_STORAGE)) {
            Environment.getExternalStorageDirectory().absolutePath +
                    userShortPath.substring(PHONE_STORAGE.length)
        } else userShortPath
    }

    fun getRootPaths(showAsUsers: Boolean): Array<String> {
        var roots = listOf<String>(
            DI.app().applicationInfo.dataDir,
            Environment.getExternalStorageDirectory().absolutePath
        )
        if (showAsUsers) {
            roots = roots.map {
                shortPathName(it).toString()
            }
        }
        return roots.toTypedArray()
    }

    class ColoringUserPath : EmptyTextWatcher() {
        private val context: Context = DI.app()
        private val colorScheme = DI.theme().colorScheme().androidColorScheme
        override fun afterTextChanged(s: Editable?) {


            if (s.toString().lowercase(Locale.getDefault())
                    .startsWith(APPDATA.lowercase(Locale.getDefault()))
            ) {
                s?.setSpan(
                    colorScheme,
                    0,
                    APPDATA.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                return
            }
            if (s.toString().lowercase(Locale.getDefault()).startsWith(
                    PHONE_STORAGE.lowercase(
                        Locale.getDefault()
                    )
                )
            ) {
                s!!.setSpan(
                    colorScheme,
                    0,
                    PHONE_STORAGE.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                return
            }
            for (span in s!!.getSpans(0, s.length, ForegroundColorSpan::class.java)) {
                s.removeSpan(span)
            }
        }
    }
}