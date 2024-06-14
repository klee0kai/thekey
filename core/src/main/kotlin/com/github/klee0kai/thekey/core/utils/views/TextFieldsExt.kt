package com.github.klee0kai.thekey.core.utils.views

import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
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

@Composable
fun TextFieldDefaults.grayColors() = TextFieldDefaults.colors().copy(
    focusedIndicatorColor = Color.Transparent,
    errorIndicatorColor = Color.Transparent,
    disabledIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
)

@Composable
fun TextFieldDefaults.transparentColors() = grayColors().copy(
    disabledContainerColor = Color.Transparent,
    errorContainerColor = Color.Transparent,
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    disabledTextColor = Color.Transparent,
)