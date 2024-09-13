package com.github.klee0kai.thekey.core.utils.views

import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun LazyListState.appBarVisible(): State<Boolean> {
    val scrollProcess by interactionSource.collectIsDraggedAsState()
    val appBarVisibleTarget = rememberDerivedStateOf { !scrollProcess || !canScrollBackward }
    val appBarVisible = remember { mutableStateOf(appBarVisibleTarget.value) }
    LaunchedEffect(appBarVisibleTarget.value) {
        if (appBarVisibleTarget.value) {
            if (canScrollBackward) delay(0.3.seconds)
            appBarVisible.value = true
        } else {
            appBarVisible.value = false
        }
    }

    return appBarVisible
}


@Composable
fun LazyListState.animateAppBarVisible(): State<TargetAlpha<Boolean>> {
    val appBarVisible by appBarVisible()
    return rememberTargetCrossFaded { appBarVisible }
}