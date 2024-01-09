package com.github.klee0kai.thekey.app.utils.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.Composable

@Composable
fun animateAlphaAsState(
    boolean: Boolean,
    label: String = "",
) = animateFloatAsState(
    targetValue = if (boolean) 1f else 0f,
    label = label
)