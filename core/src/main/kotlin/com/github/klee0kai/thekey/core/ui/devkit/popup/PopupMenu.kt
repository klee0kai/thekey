package com.github.klee0kai.thekey.core.ui.devkit.popup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.overlay.LocalOverlayProvider
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.possitions.ViewPositionDp
import com.github.klee0kai.thekey.core.utils.possitions.ViewPositionPx
import com.github.klee0kai.thekey.core.utils.possitions.onGlobalPositionState
import com.github.klee0kai.thekey.core.utils.possitions.placeTo
import com.github.klee0kai.thekey.core.utils.possitions.rememberViewPosition
import com.github.klee0kai.thekey.core.utils.possitions.toDp
import com.github.klee0kai.thekey.core.utils.views.DebugDarkPreview
import com.github.klee0kai.thekey.core.utils.views.animateAlphaAsState
import com.github.klee0kai.thekey.core.utils.views.rememberDerivedStateOf
import com.github.klee0kai.thekey.core.utils.views.tappable
import com.github.klee0kai.thekey.core.utils.views.thenIf

@Composable
fun PopupMenu(
    visible: Boolean,
    positionAnchor: State<ViewPositionPx?>,
    horizontalBias: Float = 0f,
    onDismissRequest: (() -> Unit)? = null,
    shadowColor: Color = LocalTheme.current.colorScheme.popupMenu.shadowColor,
    content: @Composable BoxScope.() -> Unit = {},
) {
    val overlayContainer = LocalOverlayProvider.current
    val density = LocalDensity.current
    val view = LocalView.current
    val bias = if (LocalLayoutDirection.current == LayoutDirection.Ltr) horizontalBias else 1f - horizontalBias
    val anchor = positionAnchor.value?.toDp(density) ?: return
    val contentPosPx = remember { mutableStateOf<ViewPositionPx?>(null) }
    val contentPosDp by rememberDerivedStateOf { contentPosPx.value?.toDp(density) ?: ViewPositionDp() }
    val offset by rememberDerivedStateOf {
        with(density) {
            when {
                view.height.toDp() < anchor.globalPos.y + anchor.size.height + contentPosDp.size.height -> {
                    DpOffset(
                        x = anchor.globalPos.x + (anchor.size.width - contentPosDp.size.width) * bias,
                        y = anchor.globalPos.y - contentPosDp.size.height,
                    )
                }

                else -> {
                    DpOffset(
                        x = anchor.globalPos.x + (anchor.size.width - contentPosDp.size.width) * bias,
                        y = anchor.globalPos.y + anchor.size.height,
                    )
                }
            }
        }
    }

    val visibleAnimated by animateAlphaAsState(visible)
    if (visibleAnimated > 0f) {
        val leftPopupShadow by rememberDerivedStateOf {
            with(density) {
                ViewPositionDp(
                    globalPos = DpOffset(x = 0.dp, y = contentPosDp.globalPos.y),
                    size = DpSize(width = contentPosDp.globalPos.x, height = contentPosDp.size.height)
                )
            }
        }

        val rightPopupShadow by rememberDerivedStateOf {
            with(density) {
                ViewPositionDp(
                    globalPos = DpOffset(x = contentPosDp.globalPos.x + contentPosDp.size.width, y = contentPosDp.globalPos.y),
                    size = DpSize(width = view.width.toDp() - (contentPosDp.globalPos.x + contentPosDp.size.width), height = contentPosDp.size.height)
                )
            }
        }

        val leftAnchorShadow by rememberDerivedStateOf {
            with(density) {
                ViewPositionDp(
                    globalPos = DpOffset(x = 0.dp, y = anchor.globalPos.y),
                    size = DpSize(width = anchor.globalPos.x, height = anchor.size.height)
                )
            }
        }

        val rightAnchorShadow by rememberDerivedStateOf {
            with(density) {
                ViewPositionDp(
                    globalPos = DpOffset(x = anchor.globalPos.x + anchor.size.width, y = anchor.globalPos.y),
                    size = DpSize(width = view.width.toDp() - (anchor.globalPos.x + anchor.size.width), height = anchor.size.height)
                )
            }
        }

        val bottomShadow by rememberDerivedStateOf {
            with(density) {
                val y = max(anchor.globalPos.y + anchor.size.height, contentPosDp.globalPos.y + contentPosDp.size.height)
                ViewPositionDp(
                    globalPos = DpOffset(x = 0.dp, y = y),
                    size = DpSize(width = view.width.toDp(), height = view.height.toDp() - y)
                )
            }
        }

        val topShadow by rememberDerivedStateOf {
            with(density) {
                ViewPositionDp(
                    globalPos = DpOffset(x = 0.dp, y = 0.dp),
                    size = DpSize(width = view.width.toDp(), height = min(anchor.globalPos.y, contentPosDp.globalPos.y))
                )
            }
        }

        overlayContainer.Overlay(content.hashCode()) {
            Box(
                modifier = Modifier
                    .thenIf(contentPosPx.value == null) { alpha(0f) }
                    .alpha(visibleAnimated)
                    .sizeIn(maxWidth = anchor.size.width)
                    .absoluteOffset(offset.x, offset.y)
                    .onGlobalPositionState(contentPosPx),
            ) {
                content()
            }

            Box(
                modifier = Modifier
                    .thenIf(contentPosPx.value == null) { alpha(0f) }
                    .alpha(visibleAnimated)
                    .placeTo(leftPopupShadow)
                    .thenIf(onDismissRequest != null) { tappable { onDismissRequest?.invoke() } }
                    .background(shadowColor)
            )

            Box(
                modifier = Modifier
                    .thenIf(contentPosPx.value == null) { alpha(0f) }
                    .alpha(visibleAnimated)
                    .placeTo(rightPopupShadow)
                    .thenIf(onDismissRequest != null) { tappable { onDismissRequest?.invoke() } }
                    .background(shadowColor)
            )
            Box(
                modifier = Modifier
                    .thenIf(contentPosPx.value == null) { alpha(0f) }
                    .alpha(visibleAnimated)
                    .placeTo(leftAnchorShadow)
                    .thenIf(onDismissRequest != null) { tappable { onDismissRequest?.invoke() } }
                    .background(shadowColor)
            )

            Box(
                modifier = Modifier
                    .thenIf(contentPosPx.value == null) { alpha(0f) }
                    .alpha(visibleAnimated)
                    .placeTo(rightAnchorShadow)
                    .thenIf(onDismissRequest != null) { tappable { onDismissRequest?.invoke() } }
                    .background(shadowColor)
            )

            Box(
                modifier = Modifier
                    .thenIf(contentPosPx.value == null) { alpha(0f) }
                    .alpha(visibleAnimated)
                    .placeTo(bottomShadow)
                    .thenIf(onDismissRequest != null) { tappable { onDismissRequest?.invoke() } }
                    .background(shadowColor)
            )

            Box(
                modifier = Modifier
                    .thenIf(contentPosPx.value == null) { alpha(0f) }
                    .alpha(visibleAnimated)
                    .placeTo(topShadow)
                    .thenIf(onDismissRequest != null) { tappable { onDismissRequest?.invoke() } }
                    .background(shadowColor)
            )
        }
    }
}


@OptIn(DebugOnly::class)
@Composable
@Preview
fun PopupMenuPreview() = DebugDarkPreview(
    layoutDirection = LayoutDirection.Ltr,
) {
    val theme = LocalTheme.current
    val textFieldPos = rememberViewPosition()
    var popupVisible by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 20.dp)
                .onGlobalPositionState(textFieldPos)
                .background(Color.Gray)
                .clickable { popupVisible = true },
        )
        Spacer(modifier = Modifier.weight(1f))
    }

    PopupMenu(
        visible = popupVisible,
        positionAnchor = textFieldPos,
        horizontalBias = 0.8f,
        onDismissRequest = {
            popupVisible = false
        },
    ) {
        Box(
            modifier = Modifier
                .height(300.dp)
                .fillMaxWidth(0.6f)
                .background(theme.colorScheme.popupMenu.surfaceColor),
        )
    }
}