package com.github.klee0kai.thekey.app.ui.designkit.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.designkit.components.SimpleScaffoldConst.dragHandleSize
import com.github.klee0kai.thekey.app.utils.views.accelerateDecelerate
import com.github.klee0kai.thekey.app.utils.views.pxToDp
import com.github.klee0kai.thekey.app.utils.views.ratioBetween
import com.github.klee0kai.thekey.app.utils.views.rememberDerivedStateOf

internal object SimpleScaffoldConst {
    val dragHandleSize = 48.dp
}


@Preview
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SimpleBottomSheetScaffold(
    modifier: Modifier = Modifier,
    simpleBottomSheetScaffoldState: SimpleBottomSheetScaffoldState = rememberSimpleBottomSheetScaffoldState(),
    onDrag: (Float) -> Unit = {},
    topContent: @Composable () -> Unit = {},
    sheetContent: @Composable () -> Unit = {},
) {
    val view = LocalView.current
    val appBarSize = simpleBottomSheetScaffoldState.appBarSize
    val topContentSize = simpleBottomSheetScaffoldState.topContentSize
    val colorScheme = remember { DI.theme().colorScheme().androidColorScheme }
    val dragProgress = remember { mutableFloatStateOf(0f) }

    val viewHeight = if (view.isInEditMode) 900.dp else view.height.pxToDp()
    val sheetMinSize = remember(viewHeight, simpleBottomSheetScaffoldState) { maxOf(viewHeight - appBarSize - topContentSize, 0.dp) }
    val sheetMaxSize = remember(viewHeight, simpleBottomSheetScaffoldState) { maxOf(viewHeight - appBarSize, 0.dp) }

    val scaffoldTopOffset = runCatching {
        simpleBottomSheetScaffoldState.scaffoldState.bottomSheetState.requireOffset().pxToDp()
    }.getOrElse { 0.dp }

    scaffoldTopOffset.ratioBetween(
        start = appBarSize,
        end = appBarSize + topContentSize
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
            scaffoldState = simpleBottomSheetScaffoldState.scaffoldState,
            sheetPeekHeight = sheetMinSize,
            sheetMaxWidth = view.width.pxToDp(),
            contentColor = colorScheme.onBackground,
            containerColor = colorScheme.background,
            content = { innerPadding ->
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxWidth()
                        .height(topContentSize)
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = appBarSize)
                            .fillMaxSize()
                    ) {
                        topContent.invoke()
                    }
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


