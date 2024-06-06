@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalFoundationApi::class)

package com.github.klee0kai.thekey.core.ui.devkit.bottomsheet

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalColorScheme
import com.github.klee0kai.thekey.core.ui.devkit.bottomsheet.SimpleScaffoldConst.dragHandleSize
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarConst
import com.github.klee0kai.thekey.core.utils.views.accelerateDecelerate
import com.github.klee0kai.thekey.core.utils.views.pxToDp
import com.github.klee0kai.thekey.core.utils.views.ratioBetween
import com.github.klee0kai.thekey.core.utils.views.rememberDerivedStateOf
import org.jetbrains.annotations.VisibleForTesting

@Composable
fun BottomSheetBigDialog(
    modifier: Modifier = Modifier,
    sheetDragHandleModifier: Modifier = Modifier,
    sheetModifier: Modifier = Modifier,
    topMargin: Dp = 0.dp,
    sheetPeekHeight: Dp = 400.dp,
    scaffoldState: BottomSheetScaffoldState = rememberSafeBottomSheetScaffoldState(skipHiddenState = false),
    onDrag: (Float) -> Unit = {},
    sheetContent: @Composable () -> Unit = {},
) {
    val view = LocalView.current
    val viewHeight = view.height.pxToDp()
    val colorScheme = LocalColorScheme.current.androidColorScheme
    val dragProgress = remember { mutableFloatStateOf(0f) }

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
        start = topMargin,
        end = topContentSize
    ).coerceIn(0f, 1f)
        .also { newDrag ->
            if (newDrag != dragProgress.floatValue) {
                dragProgress.floatValue = newDrag
                onDrag(newDrag)
            }
        }

    CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
        BottomSheetScaffold(
            modifier = modifier,
            scaffoldState = scaffoldState,
            sheetPeekHeight = sheetPeekHeight,
            sheetMaxWidth = view.width.pxToDp(),
            contentColor = Color.Transparent,
            containerColor = Color.Transparent,
            content = { _ -> },
            sheetShape = BottomSheetDefaults.ExpandedShape,
            sheetDragHandle = {
                Box(
                    modifier = sheetDragHandleModifier
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
            sheetContainerColor = colorScheme.surface,
            sheetContentColor = colorScheme.onSurface,
            sheetContent = {
                Box(
                    modifier = sheetModifier
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


@VisibleForTesting
@Preview
@Composable
fun BottomSheetBigDialogPreview() = AppTheme {
    Box(modifier = Modifier.background(Color.Yellow)) {
        BottomSheetBigDialog(
            topMargin = AppBarConst.appBarSize
        )
    }
}
