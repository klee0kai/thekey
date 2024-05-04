package com.github.klee0kai.thekey.app.ui.designkit.components.bottomsheet

import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.utils.views.accelerateDecelerate
import com.github.klee0kai.thekey.app.utils.views.ratioBetween

fun Float.topContentAlphaFromDrag() =
    ratioBetween(0.3f, 0.9f)
        .coerceIn(0f, 1f)
        .accelerateDecelerate()

fun Float.topContentOffsetFromDrag() =
    ratioBetween(1f, 0f)
        .coerceIn(0f, 1f)
        .accelerateDecelerate()
        .let { 30.dp * -it }
