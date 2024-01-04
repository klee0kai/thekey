@file:OptIn(ExperimentalComposeUiApi::class, ExperimentalComposeUiApi::class)

package com.github.klee0kai.thekey.app.utils.views

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter

fun Modifier.onTouch(block: () -> Unit) =
    pointerInteropFilter {
        block.invoke()
        false
    }