package com.github.klee0kai.thekey.core.ui.devkit.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarConst
import com.github.klee0kai.thekey.core.ui.devkit.components.dropdownfields.SimpleSelectPopupMenu
import com.github.klee0kai.thekey.core.ui.devkit.components.text.AppTextField
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.possitions.ViewPositionDp
import com.github.klee0kai.thekey.core.utils.possitions.ViewPositionPx
import com.github.klee0kai.thekey.core.utils.possitions.onGlobalPositionState
import com.github.klee0kai.thekey.core.utils.possitions.placeTo
import com.github.klee0kai.thekey.core.utils.possitions.pxToDp
import com.github.klee0kai.thekey.core.utils.possitions.rememberViewPosition
import com.github.klee0kai.thekey.core.utils.possitions.toDp
import com.github.klee0kai.thekey.core.utils.views.DebugDarkScreenPreview
import com.github.klee0kai.thekey.core.utils.views.animateAlphaAsState
import com.github.klee0kai.thekey.core.utils.views.horizontal
import com.github.klee0kai.thekey.core.utils.views.rememberAlphaAnimate
import com.github.klee0kai.thekey.core.utils.views.rememberDerivedStateOf
import com.github.klee0kai.thekey.core.utils.views.tappable
import com.github.klee0kai.thekey.core.utils.views.thenIf
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

private enum class PopupGravity {
    TOP,
    BOTTOM,
}

@Composable
fun PopupMenu(
    visible: Boolean,
    positionAnchor: State<ViewPositionPx?>,
    horizontalBias: Float = 0f,
    onDismissRequest: (() -> Unit)? = null,
    shadowColor: Color = LocalTheme.current.colorScheme.popupMenu.shadowColor,
    ignoreAnchorSize: Boolean = false,
    content: @Composable BoxScope.() -> Unit = {},
) {
    val visibleAlpha by animateAlphaAsState(visible)
    val theme = LocalTheme.current

    if (visibleAlpha > 0f) {
        val overlayProvider = LocalOverlayProvider.current
        val safeContentPaddings = WindowInsets.safeContent.asPaddingValues()
        val overlayKey = listOf(content.hashCode())
        DisposableEffect(key1 = Unit) {
            onDispose { overlayProvider.clean(overlayKey) }
        }

        LocalOverlayProvider.current.Overlay(overlayKey) {
            val density = LocalDensity.current
            val view = LocalView.current
            val bias = if (LocalLayoutDirection.current == LayoutDirection.Ltr) {
                horizontalBias
            } else {
                1f - horizontalBias
            }
            val anchorDp by rememberDerivedStateOf { positionAnchor.value?.toDp(density) }
            val contentPosPx = remember { mutableStateOf<ViewPositionPx?>(null) }
            val contentPosDp by rememberDerivedStateOf {
                contentPosPx.value?.toDp(density) ?: ViewPositionDp()
            }
            val gravity by rememberDerivedStateOf {
                with(density) {
                    val anchor = anchorDp ?: return@rememberDerivedStateOf null

                    when {
                        view.height.toDp() - safeContentPaddings.calculateBottomPadding()
                                < anchor.globalPos.y + anchor.size.height + contentPosDp.size.height
                                && anchor.globalPos.y > view.height.toDp() / 2f -> PopupGravity.TOP

                        else -> PopupGravity.BOTTOM
                    }
                }
            }
            val offset by rememberDerivedStateOf {
                with(density) {
                    val anchor = anchorDp ?: return@rememberDerivedStateOf DpOffset(0.dp, 0.dp)

                    var offset = when (gravity) {
                        PopupGravity.TOP -> {
                            DpOffset(
                                x = anchor.globalPos.x + (anchor.size.width - contentPosDp.size.width) * bias,
                                y = anchor.globalPos.y - contentPosDp.size.height,
                            )
                        }

                        PopupGravity.BOTTOM -> {
                            DpOffset(
                                x = anchor.globalPos.x + (anchor.size.width - contentPosDp.size.width) * bias,
                                y = anchor.globalPos.y + anchor.size.height,
                            )
                        }

                        else -> return@rememberDerivedStateOf DpOffset(0.dp, 0.dp)
                    }
                    if (offset.x + contentPosDp.size.width > view.width.toDp() - safeContentPaddings.horizontal()) {
                        offset = offset.copy(
                            x = view.width.toDp() - safeContentPaddings.horizontal() - contentPosDp.size.width
                        )
                    }

                    offset
                }
            }
            val contentMaxHeight by rememberDerivedStateOf {
                with(density) {
                    val anchor = anchorDp ?: return@rememberDerivedStateOf 0.dp
                    when (gravity) {
                        PopupGravity.TOP -> {
                            anchor.globalPos.y - safeContentPaddings.calculateTopPadding()
                        }

                        PopupGravity.BOTTOM -> {
                            view.height.toDp() - safeContentPaddings.calculateBottomPadding() - anchor.globalPos.y - anchor.size.height
                        }

                        null -> 0.dp
                    }
                }
            }

            val leftPopupShadow by rememberDerivedStateOf {
                with(density) {
                    ViewPositionDp(
                        globalPos = DpOffset(x = 0.dp, y = contentPosDp.globalPos.y),
                        size = DpSize(
                            width = contentPosDp.globalPos.x,
                            height = contentPosDp.size.height
                        )
                    )
                }
            }

            val rightPopupShadow by rememberDerivedStateOf {
                with(density) {
                    ViewPositionDp(
                        globalPos = DpOffset(
                            x = contentPosDp.globalPos.x + contentPosDp.size.width,
                            y = contentPosDp.globalPos.y
                        ),
                        size = DpSize(
                            width = view.width.toDp() - (contentPosDp.globalPos.x + contentPosDp.size.width),
                            height = contentPosDp.size.height
                        )
                    )
                }
            }

            val leftAnchorShadow by rememberDerivedStateOf {
                with(density) {
                    val anchor = anchorDp ?: return@rememberDerivedStateOf ViewPositionDp()
                    ViewPositionDp(
                        globalPos = DpOffset(x = 0.dp, y = anchor.globalPos.y),
                        size = DpSize(width = anchor.globalPos.x, height = anchor.size.height)
                    )
                }
            }

            val rightAnchorShadow by rememberDerivedStateOf {
                with(density) {
                    val anchor = anchorDp ?: return@rememberDerivedStateOf ViewPositionDp()
                    ViewPositionDp(
                        globalPos = DpOffset(
                            x = anchor.globalPos.x + anchor.size.width,
                            y = anchor.globalPos.y
                        ),
                        size = DpSize(
                            width = view.width.toDp() - (anchor.globalPos.x + anchor.size.width),
                            height = anchor.size.height
                        )
                    )
                }
            }

            val bottomShadow by rememberDerivedStateOf {
                with(density) {
                    val anchor = anchorDp ?: return@rememberDerivedStateOf ViewPositionDp()
                    val y = max(
                        anchor.globalPos.y + anchor.size.height,
                        contentPosDp.globalPos.y + contentPosDp.size.height
                    )
                    ViewPositionDp(
                        globalPos = DpOffset(x = 0.dp, y = y),
                        size = DpSize(width = view.width.toDp(), height = view.height.toDp() - y)
                    )
                }
            }

            val topShadow by rememberDerivedStateOf {
                with(density) {
                    val anchor = anchorDp ?: return@rememberDerivedStateOf ViewPositionDp()
                    ViewPositionDp(
                        globalPos = DpOffset(x = 0.dp, y = 0.dp),
                        size = DpSize(
                            width = view.width.toDp(),
                            height = min(anchor.globalPos.y, contentPosDp.globalPos.y)
                        )
                    )
                }
            }

            val positionAvailableAlpha by rememberAlphaAnimate { contentPosPx.value != null && visible }
            val fullAnimatedAlpha by rememberDerivedStateOf { positionAvailableAlpha * visibleAlpha }
            Box(
                modifier = Modifier
                    .thenIf(!ignoreAnchorSize) {
                        sizeIn(
                            maxWidth = anchorDp?.size?.width ?: 0.dp,
                            maxHeight = contentMaxHeight ?: 0.dp,
                        )
                    }
                    .absoluteOffset(offset.x, offset.y)
                    .onGlobalPositionState(contentPosPx)
                    .background(shadowColor.copy(alpha = shadowColor.alpha * fullAnimatedAlpha))
                    .alpha(fullAnimatedAlpha),
            ) {
                CompositionLocalProvider(
                    LocalTextStyle provides theme.typeScheme.header,
                ) {
                    content()
                }
            }

            Box(
                modifier = Modifier
                    .placeTo(leftPopupShadow)
                    .thenIf(onDismissRequest != null) { tappable { onDismissRequest?.invoke() } }
                    .alpha(fullAnimatedAlpha)
                    .background(shadowColor)
            )

            Box(
                modifier = Modifier
                    .placeTo(rightPopupShadow)
                    .thenIf(onDismissRequest != null) { tappable { onDismissRequest?.invoke() } }
                    .alpha(fullAnimatedAlpha)
                    .background(shadowColor)
            )
            Box(
                modifier = Modifier
                    .placeTo(leftAnchorShadow)
                    .thenIf(onDismissRequest != null) { tappable { onDismissRequest?.invoke() } }
                    .alpha(fullAnimatedAlpha)
                    .background(shadowColor)
            )

            Box(
                modifier = Modifier
                    .placeTo(rightAnchorShadow)
                    .thenIf(onDismissRequest != null) { tappable { onDismissRequest?.invoke() } }
                    .alpha(fullAnimatedAlpha)
                    .background(shadowColor)
            )

            Box(
                modifier = Modifier
                    .placeTo(bottomShadow)
                    .thenIf(onDismissRequest != null) { tappable { onDismissRequest?.invoke() } }
                    .alpha(fullAnimatedAlpha)
                    .background(shadowColor)
            )

            Box(
                modifier = Modifier
                    .placeTo(topShadow)
                    .thenIf(onDismissRequest != null) { tappable { onDismissRequest?.invoke() } }
                    .alpha(fullAnimatedAlpha)
                    .background(shadowColor)
            )
        }
    }
}


@OptIn(DebugOnly::class)
@Composable
@Preview
fun PopupMenuSimplePreview() = DebugDarkScreenPreview(
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


@OptIn(DebugOnly::class)
@Composable
@Preview
fun PopupMenuSmallButtonPreview() = DebugDarkScreenPreview(
    layoutDirection = LayoutDirection.Ltr,
) {
    val theme = LocalTheme.current
    val textFieldPos = rememberViewPosition()
    var popupVisible by remember { mutableStateOf(true) }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        val (buttonField) = createRefs()
        Box(
            modifier = Modifier
                .size(width = 50.dp, height = 50.dp)
                .onGlobalPositionState(textFieldPos)
                .background(Color.Gray)
                .clickable { popupVisible = true }
                .constrainAs(buttonField) {
                    linkTo(
                        top = parent.top,
                        bottom = parent.bottom,
                        start = parent.start,
                        end = parent.end,
                        verticalBias = 0.5f,
                        horizontalBias = 1f,
                    )
                },
        )
    }

    PopupMenu(
        visible = popupVisible,
        positionAnchor = textFieldPos,
        horizontalBias = 0.2f,
        ignoreAnchorSize = true,
        onDismissRequest = { popupVisible = false },
    ) {
        SimpleSelectPopupMenu(
            variants = listOf(
                "item 1",
                "item 2",
                "item 3",
            )
        )
    }
}


@OptIn(DebugOnly::class)
@Composable
@Preview
fun PopupMenuInsetsPreview() = DebugDarkScreenPreview(
    layoutDirection = LayoutDirection.Ltr,
) {
    val theme = LocalTheme.current
    val view = LocalView.current
    var popupVisible by remember { mutableStateOf(true) }
    val storagePathPosition = rememberViewPosition()
    val safeContentPaddings = WindowInsets.safeContent.asPaddingValues()
    var additionalPadding by remember { mutableStateOf(0.dp) }

    LaunchedEffect(Unit) {
        while (isActive) {
            repeat(60) {
                delay(100)
                additionalPadding += 10.dp
            }
            repeat(60) {
                delay(100)
                additionalPadding -= 10.dp
            }
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .imePadding()
            .fillMaxSize()
            .defaultMinSize(minHeight = view.height.pxToDp()),
    ) {
        val (
            pathTextField,
        ) = createRefs()

        AppTextField(
            modifier = Modifier
                .onFocusChanged { }
                .onGlobalPositionState(storagePathPosition)
                .constrainAs(pathTextField) {
                    width = Dimension.fillToConstraints
                    linkTo(
                        start = parent.start,
                        top = parent.top,
                        end = parent.end,
                        bottom = parent.bottom,
                        verticalBias = 0f,
                        topMargin = 8.dp + AppBarConst.appBarSize + additionalPadding,
                        startMargin = safeContentPaddings.horizontal(16.dp),
                        endMargin = safeContentPaddings.horizontal(16.dp)
                    )
                },
            value = "text",
            label = { "text" }
        )
    }

    PopupMenu(
        visible = popupVisible,
        positionAnchor = storagePathPosition,
        horizontalBias = 0.8f,
        onDismissRequest = { popupVisible = false },
    ) {
        Box(
            modifier = Modifier
                .height(300.dp)
                .fillMaxWidth(0.6f)
                .background(theme.colorScheme.popupMenu.surfaceColor),
        )
    }
}

@OptIn(DebugOnly::class)
@Composable
@Preview
fun PopupMenuMaxSizePreview() = DebugDarkScreenPreview(
    layoutDirection = LayoutDirection.Ltr,
) {
    val theme = LocalTheme.current
    val view = LocalView.current
    var popupVisible by remember { mutableStateOf(true) }
    val storagePathPosition = rememberViewPosition()
    val safeContentPaddings = WindowInsets.safeContent.asPaddingValues()
    var additionalPadding by remember { mutableStateOf(0.dp) }

    LaunchedEffect(Unit) {
        while (isActive) {
            repeat(60) {
                delay(100)
                additionalPadding += 10.dp
            }
            repeat(60) {
                delay(100)
                additionalPadding -= 10.dp
            }
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .imePadding()
            .fillMaxSize()
            .defaultMinSize(minHeight = view.height.pxToDp()),
    ) {
        val (
            pathTextField,
        ) = createRefs()

        AppTextField(
            modifier = Modifier
                .onFocusChanged { }
                .onGlobalPositionState(storagePathPosition)
                .constrainAs(pathTextField) {
                    width = Dimension.fillToConstraints
                    linkTo(
                        start = parent.start,
                        top = parent.top,
                        end = parent.end,
                        bottom = parent.bottom,
                        verticalBias = 0f,
                        topMargin = 8.dp + AppBarConst.appBarSize + additionalPadding,
                        startMargin = safeContentPaddings.horizontal(16.dp),
                        endMargin = safeContentPaddings.horizontal(16.dp)
                    )
                },
            value = "text",
            label = { "text" }
        )
    }

    PopupMenu(
        visible = popupVisible,
        positionAnchor = storagePathPosition,
        horizontalBias = 0.8f,
        onDismissRequest = { popupVisible = false },
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.6f)
                .padding(vertical = 10.dp)
                .background(theme.colorScheme.popupMenu.surfaceColor),
        )
    }
}