@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalFoundationApi::class)

package com.github.klee0kai.thekey.app.ui.designkit.components.bottomsheet

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.ui.designkit.AppTheme
import com.github.klee0kai.thekey.app.ui.designkit.LocalColorScheme
import com.github.klee0kai.thekey.app.ui.designkit.components.appbar.AppBarConst
import com.github.klee0kai.thekey.app.ui.designkit.components.bottomsheet.SimpleScaffoldConst.dragHandleSize
import com.github.klee0kai.thekey.app.utils.views.accelerateDecelerate
import com.github.klee0kai.thekey.app.utils.views.pxToDp
import com.github.klee0kai.thekey.app.utils.views.ratioBetween
import com.github.klee0kai.thekey.app.utils.views.rememberDerivedStateOf
import org.jetbrains.annotations.VisibleForTesting

internal object SimpleScaffoldConst {
    val dragHandleSize = 48.dp
}

@Composable
fun SimpleBottomSheetScaffold(
    modifier: Modifier = Modifier,
    topContentSize: Dp = 190.dp,
    topMargin: Dp = 0.dp,
    scaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState(),
    onDrag: (Float) -> Unit = {},
    topContent: @Composable () -> Unit = {},
    sheetContent: @Composable () -> Unit = {},
) {
    val view = LocalView.current
    val colorScheme = LocalColorScheme.current.androidColorScheme
    val dragProgress = remember { mutableFloatStateOf(0f) }

    val viewHeight = view.height.pxToDp()
    val sheetMinSize = remember(viewHeight, topMargin, topContentSize) { maxOf(viewHeight - topMargin - topContentSize, 0.dp) }
    val sheetMaxSize = remember(viewHeight, topMargin) { maxOf(viewHeight - topMargin, 0.dp) }

    val scaffoldTopOffset = runCatching {
        scaffoldState.bottomSheetState.requireOffset().pxToDp()
    }.getOrElse { 0.dp }

    scaffoldTopOffset.ratioBetween(
        start = topMargin,
        end = topMargin + topContentSize
    ).coerceIn(0f, 1f)
        .also { newDrag ->
            if (newDrag != dragProgress.floatValue) {
                dragProgress.floatValue = newDrag
                onDrag(newDrag)
            }
        }

    val dragAlpha by rememberDerivedStateOf {
        dragProgress.floatValue.ratioBetween(
            start = 0.2f,
            end = 0.5f,
        ).coerceIn(0f, 1f)
            .accelerateDecelerate()
    }

    CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
        BottomSheetScaffold(
            modifier = modifier,
            scaffoldState = scaffoldState,
            sheetPeekHeight = sheetMinSize,
            sheetMaxWidth = view.width.pxToDp(),
            contentColor = colorScheme.onBackground,
            containerColor = colorScheme.background,
            content = { innerPadding ->
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(top = topMargin)
                        .fillMaxWidth()
                        .height(topContentSize)
                ) {
                    topContent.invoke()
                }
            },
            sheetShape = BottomSheetDefaults.ExpandedShape,
            sheetDragHandle = {
                Box(
                    modifier = Modifier
                        .height(dragHandleSize),
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
            sheetContainerColor = colorScheme.surface,
            sheetContentColor = colorScheme.onSurface,
            sheetContent = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(sheetMaxSize),
                    content = {
                        sheetContent.invoke()
                    }
                )
            }
        )
    }
}


@VisibleForTesting
@Preview
@Composable
fun SimpleBottomSheetScaffoldPreview() = AppTheme {
    SimpleBottomSheetScaffold()
}

@VisibleForTesting
@Preview
@Composable
fun SimpleBottomSheetScaffoldTopContentPreview() = AppTheme {
    SimpleBottomSheetScaffold(
        topContent = {
            Box(
                modifier = Modifier
                    .background(Color.Green.copy(alpha = 0.5f))
                    .fillMaxHeight()
                    .width(100.dp)
            )
        }
    )
}

@VisibleForTesting
@Preview
@Composable
fun SimpleBottomSheetScaffoldTopContent2Preview() = AppTheme {
    SimpleBottomSheetScaffold(
        topContentSize = 190.dp,
        topMargin = AppBarConst.appBarSize,
        topContent = {
            Box(
                modifier = Modifier
                    .background(Color.Green.copy(alpha = 0.5f))
                    .fillMaxHeight()
                    .width(100.dp)
            )
        }
    )
    Box(
        modifier = Modifier
            .padding(top = AppBarConst.appBarSize)
            .background(Color.Red.copy(alpha = 0.5f))
            .height(190.dp)
            .width(50.dp)
    )
}