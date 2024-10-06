@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,
    ExperimentalFoundationApi::class
)

package com.github.klee0kai.thekey.core.ui.devkit.bottomsheet

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.wear.compose.material.Text
import com.github.klee0kai.thekey.core.di.CoreDI
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.bottomsheet.SimpleScaffoldConst.dragHandleSize
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarConst
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.possitions.pxToDp
import com.github.klee0kai.thekey.core.utils.views.DebugDarkScreenPreview
import com.github.klee0kai.thekey.core.utils.views.accelerateDecelerate
import com.github.klee0kai.thekey.core.utils.views.createDialogBottomAnchor
import com.github.klee0kai.thekey.core.utils.views.ratioBetween
import com.github.klee0kai.thekey.core.utils.views.rememberDerivedStateOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.jetbrains.annotations.VisibleForTesting
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun BottomSheetBigDialog(
    modifier: Modifier = Modifier,
    sheetDragHandleModifier: Modifier = Modifier,
    sheetModifier: Modifier = Modifier,
    topMargin: Dp = 0.dp,
    sheetPeekHeight: Dp = 400.dp,
    scaffoldState: BottomSheetScaffoldState = rememberSafeBottomSheetScaffoldState(skipHiddenState = false),
    onDrag: (Float) -> Unit = {},
    onClosed: () -> Unit = {},
    sheetContent: @Composable () -> Unit = {},
) {
    val view = LocalView.current
    val fastScope = CoreDI.androidFastUiScope()
    val viewHeight = view.height.pxToDp()
    val theme = LocalTheme.current
    val colorScheme = theme.colorScheme.androidColorScheme
    val dragProgress = remember { mutableFloatStateOf(0f) }
    val initValue = remember { scaffoldState.bottomSheetState.currentValue }

    val scaffoldTopOffset = runCatching {
        scaffoldState.bottomSheetState.requireOffset().pxToDp()
    }.getOrElse { 0.dp }

    val dragAlpha by rememberDerivedStateOf {
        dragProgress.floatValue.ratioBetween(
            start = 0.2f,
            end = 0.5f,
        ).coerceIn(0f, 1f)
            .accelerateDecelerate()
    }

    val topContentSize = viewHeight - sheetPeekHeight
    scaffoldTopOffset.ratioBetween(
        start = topMargin - dragHandleSize,
        end = topContentSize
    ).coerceIn(0f, 1f)
        .also { newDrag ->
            if (newDrag != dragProgress.floatValue) {
                dragProgress.floatValue = newDrag
                onDrag(newDrag)
            }
        }

    var showUpAlpha by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        if (initValue == SheetValue.Hidden) {
            withContext(fastScope.coroutineContext) {
                scaffoldState.bottomSheetState.hide()
            }
            showUpAlpha = 1f
            scaffoldState.bottomSheetState.partialExpand()
        } else {
            showUpAlpha = 1f
        }

        while (scaffoldState.bottomSheetState.currentValue != SheetValue.Hidden) {
            delay(100.milliseconds)
        }
        onClosed.invoke()
    }

    CompositionLocalProvider(
        LocalOverscrollConfiguration provides null,
    ) {
        BottomSheetScaffold(
            modifier = modifier
                .alpha(showUpAlpha),
            scaffoldState = scaffoldState,
            contentColor = Color.Transparent,
            containerColor = Color.Transparent,
            content = { _ -> },
            sheetShape = BottomSheetDefaults.ExpandedShape,
            sheetDragHandle = {
                Box(
                    modifier = sheetDragHandleModifier
                        .alpha(showUpAlpha)
                        .height(dragHandleSize)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 48.dp, height = 4.dp)
                            .background(
                                color = colorScheme.onSurface.copy(alpha = 0.4f * dragAlpha),
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                }
            },
            sheetTonalElevation = 0.dp,
            sheetShadowElevation = 0.dp,
            sheetPeekHeight = sheetPeekHeight,
            sheetMaxWidth = view.width.pxToDp(),
            sheetContainerColor = theme.colorScheme.cardsBackground.copy(alpha = showUpAlpha),
            sheetContentColor = colorScheme.onSurface.copy(alpha = showUpAlpha),
            sheetContent = {
                Box(
                    modifier = sheetModifier
                        .alpha(showUpAlpha)
                        .fillMaxWidth()
                        .height(viewHeight - topMargin),
                    content = {
                        sheetContent.invoke()
                    }
                )
            }
        )
    }
}


@OptIn(DebugOnly::class)
@VisibleForTesting
@Preview
@Composable
fun BottomSheetBigDialogPreview() = DebugDarkScreenPreview {
    var dragProgress by remember { mutableFloatStateOf(1f) }
    val sheetPeekHeight = 400.dp
    val safeContentPaddings = WindowInsets.safeContent.asPaddingValues()

    Box(modifier = Modifier.background(Color.Yellow)) {
        BottomSheetBigDialog(
            topMargin = AppBarConst.appBarSize,
            onDrag = { dragProgress = it },
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                val (boxField, messageField) = createRefs()
                val dialogBottom = createDialogBottomAnchor(
                    sheetPeekHeight = sheetPeekHeight,
                    dragProcess = dragProgress,
                    bottomMargin = safeContentPaddings.calculateBottomPadding(),
                )

                Box(modifier = Modifier
                    .padding(10.dp)
                    .background(Color.Black.copy(alpha = 0.4f))
                    .constrainAs(boxField) {
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                        linkTo(
                            start = parent.start,
                            end = parent.end,
                            top = parent.top,
                            bottom = dialogBottom.bottom,
                        )
                    })

                Text(
                    modifier = Modifier
                        .constrainAs(messageField) {
                            linkTo(
                                start = parent.start,
                                end = parent.end,
                                top = parent.top,
                                bottom = dialogBottom.bottom,
                            )
                        },
                    text = "drag $dragProgress"
                )
            }
        }
    }
}
