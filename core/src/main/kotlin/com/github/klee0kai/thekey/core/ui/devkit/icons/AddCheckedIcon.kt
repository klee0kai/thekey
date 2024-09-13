package com.github.klee0kai.thekey.core.ui.devkit.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.github.klee0kai.thekey.core.utils.views.animateTargetCrossFaded

@Composable
fun AddCheckedIcon(
    modifier: Modifier = Modifier,
    isAdded: Boolean = false,
) {
    val isAddedAnimated by animateTargetCrossFaded(target = isAdded)
    Icon(
        modifier = modifier
            .alpha(isAddedAnimated.alpha),
        imageVector = if (isAddedAnimated.current) {
            Icons.Default.Check
        } else {
            Icons.Default.Add
        },
        contentDescription = null
    )
}