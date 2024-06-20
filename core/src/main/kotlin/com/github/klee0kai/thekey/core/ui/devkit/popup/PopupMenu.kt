package com.github.klee0kai.thekey.core.ui.devkit.popup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.core.ui.devkit.components.text.AppTextField
import com.github.klee0kai.thekey.core.ui.devkit.overlay.LocalOverlayProvider
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.views.DebugDarkPreview
import com.github.klee0kai.thekey.core.utils.views.ViewPositionPx
import com.github.klee0kai.thekey.core.utils.views.animateTargetCrossFaded
import com.github.klee0kai.thekey.core.utils.views.onGlobalPositionState
import com.github.klee0kai.thekey.core.utils.views.rememberDerivedStateOf
import com.github.klee0kai.thekey.core.utils.views.rememberViewPosition
import com.github.klee0kai.thekey.core.utils.views.thenIf
import com.github.klee0kai.thekey.core.utils.views.toDp


@OptIn(DebugOnly::class)
@Composable
@Preview
fun PopupMenuPreview() = DebugDarkPreview(
    layoutDirection = LayoutDirection.Ltr,
) {
    val textFieldPos = rememberViewPosition()
    var popupVisible by remember { mutableStateOf(true) }
    val focusRequester = remember { FocusRequester() }

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.weight(1.7f))
        AppTextField(
            modifier = Modifier
                .focusRequester(focusRequester)
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .onGlobalPositionState(textFieldPos),
            value = "some text",
        )
        Spacer(modifier = Modifier.weight(1f))
    }

    PopupMenuInRoot(
        visible = true,
        positionAnchor = textFieldPos,
        onDismissRequest = {
            focusRequester.freeFocus()
            popupVisible = false
        },
    ) {
        Box(
            modifier = Modifier
                .background(Color.Red)
                .height(300.dp)
                .fillMaxWidth(0.6f),
        )
    }
}

@Composable
fun PopupMenuInRoot(
    visible: Boolean,
    positionAnchor: State<ViewPositionPx?>,
    horizontalBias: Float = 1f,
    onDismissRequest: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit = {},
) {
    val overlayContainer = LocalOverlayProvider.current
    val density = LocalDensity.current
    val view = LocalView.current
    val bias = if (LocalLayoutDirection.current == LayoutDirection.Ltr) horizontalBias else 1f - horizontalBias
    val anchor = positionAnchor.value?.toDp(density) ?: return
    val contentPosPx = remember { mutableStateOf<ViewPositionPx?>(null) }
    val offset by rememberDerivedStateOf {
        with(density) {
            val contentPoxDp = contentPosPx.value?.toDp(density) ?: return@rememberDerivedStateOf DpOffset(0.dp, 0.dp)
            when {
                view.height.toDp() < anchor.globalPos.y + anchor.size.height + contentPoxDp.size.height -> {
                    DpOffset(
                        x = anchor.globalPos.x + (anchor.size.width - contentPoxDp.size.width) * bias,
                        y = anchor.globalPos.y - contentPoxDp.size.height,
                    )
                }

                else -> {
                    DpOffset(
                        x = anchor.globalPos.x + (anchor.size.width - contentPoxDp.size.width) * bias,
                        y = anchor.globalPos.y + anchor.size.height,
                    )
                }
            }
        }
    }

    val visibleAnimated by animateTargetCrossFaded(target = visible)
    if (visibleAnimated.current) {
        overlayContainer.Overlay {
            Box(
                modifier = Modifier
                    .sizeIn(maxWidth = anchor.size.width)
                    .onGlobalPositionState(contentPosPx)
                    .absoluteOffset(offset.x, offset.y)
                    .thenIf(contentPosPx.value == null) { alpha(0f) }
                    .alpha(visibleAnimated.alpha),
            ) {
                content()
            }


        }
    }
}