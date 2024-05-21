package com.github.klee0kai.thekey.core.utils.views

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText

fun String.toTextFieldValue(): TextFieldValue =
    TextFieldValue(
        text = this,
        selection = TextRange(length)
    )


fun String.toAnnotationString() = AnnotatedString(this)

fun AnnotatedString.toTransformationText() =
    TransformedText(
        text = this,
        offsetMapping = OffsetMapping.Identity
    )