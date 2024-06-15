package com.github.klee0kai.thekey.app.ui.navigation

import android.content.Intent


fun createFileIntent(name: String) =
    Intent(Intent.ACTION_CREATE_DOCUMENT)
        .apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            setType("application/ckey")
            putExtra(Intent.EXTRA_TITLE, name)
            putExtra(
                Intent.EXTRA_MIME_TYPES, arrayOf(
                    "" to ""
                )
            )
        }

fun openFileIntent() =
    Intent(Intent.ACTION_OPEN_DOCUMENT)
        .apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            setType("application/*")
//            putExtra(
//                Intent.EXTRA_MIME_TYPES, arrayOf(
//                    "application/ckey" to ".ckey"
//                )
//            )
        }