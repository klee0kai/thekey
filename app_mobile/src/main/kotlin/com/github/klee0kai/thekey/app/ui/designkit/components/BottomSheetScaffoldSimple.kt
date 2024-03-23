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
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.designkit.components.SimpleScaffoldConst.dragHandleSize
import com.github.klee0kai.thekey.app.ui.navigation.LocalRouter
import com.github.klee0kai.thekey.app.utils.views.accelerateDecelerate
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
    topContent: @Composable () -> Unit = {},
    sheetContent: @Composable () -> Unit = {},
) {
    val density = LocalDensity.current
    val view = LocalView.current
    val appBarSize = simpleBottomSheetScaffoldState.appBarSize
    val topContentSize = simpleBottomSheetScaffoldState.topContentSize
    val colorScheme = remember { DI.theme().colorScheme().androidColorScheme }

    val viewHeight = remember(view.height) {
        with(density) { if (view.isInEditMode) 900.dp else view.height.toDp() }
    }
    val sheetMinSize = remember(viewHeight) {
        maxOf(viewHeight - appBarSize - topContentSize, 0.dp)
    }
    val sheetMaxSize = remember(viewHeight) { maxOf(viewHeight - appBarSize, sheetMinSize) }

    val scaffoldTopOffset = runCatching {
        with(LocalDensity.current) {
            simpleBottomSheetScaffoldState.scaffoldState.bottomSheetState.requireOffset().toDp()
        }
    }.getOrElse { 0.dp }

    simpleBottomSheetScaffoldState.dragProgress.floatValue = scaffoldTopOffset.ratioBetween(
        start = appBarSize,
        end = appBarSize + topContentSize
    ).coerceIn(0f, 1f)

    val dragAlpha by rememberDerivedStateOf {
        simpleBottomSheetScaffoldState.dragProgress.floatValue.ratioBetween(
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
            contentColor = colorScheme.onBackground,
            containerColor = colorScheme.background,
            snackbarHost = { SnackbarHost(hostState = LocalRouter.current.snackbarHostState) },
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


