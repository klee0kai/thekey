package com.github.klee0kai.thekey.app.utils.views

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

fun String.toTextFieldValue(): TextFieldValue =
    TextFieldValue(
        text = this,
        selection = TextRange(length)
    )


fun String.toAnnotationString() = AnnotatedString(this)
