package com.github.klee0kai.thekey.core.ui.devkit

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.core.ui.devkit.overlay.OverlayContainer
import com.github.klee0kai.thekey.core.utils.views.animateTargetCrossFaded
import com.github.klee0kai.thekey.core.utils.views.currentRef
import com.github.klee0kai.thekey.core.utils.views.isIme

@Composable
fun Screen(
    content: @Composable () -> Unit,
) = OverlayContainer {
    val router by LocalRouter.currentRef
    val isNavBoardOpen by router!!.isNavBoardOpen.collectAsState(false)

    val imeVisible by animateTargetCrossFaded(WindowInsets.isIme)
    BackHandler(enabled = imeVisible.current) {
        when {
            isNavBoardOpen -> router?.hideNavigationBoard()
            else -> router?.hideKeyboard()
        }
    }

    Box(
        modifier = Modifier
            .pointerInput(Unit) { detectTapGestures { router?.hideKeyboard() } }
    ) {
        content()
    }
}