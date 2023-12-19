package com.github.klee0kai.thekey.app.ui.designkit.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.designkit.components.SimpleScaffoldConst.dragHandleSize
import com.github.klee0kai.thekey.app.utils.views.accelerateDecelerate
import com.github.klee0kai.thekey.app.utils.views.ratioBetween

internal object SimpleScaffoldConst {
    val dragHandleSize = 48.dp
}

@OptIn(ExperimentalMaterial3Api::class)
class SimpleBottomSheetScaffoldState(
    val topContentSize: Dp = 190.dp,
    val appBarSize: Dp = 0.dp,
    val scaffoldState: BottomSheetScaffoldState,
    val dragProgress: MutableFloatState = mutableFloatStateOf(0f),
)


@Composable
@ExperimentalMaterial3Api
fun rememberSimpleBottomSheetScaffoldState(
    topContentSize: Dp = 190.dp,
    appBarSize: Dp = 0.dp,
    scaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState(),
): SimpleBottomSheetScaffoldState {
    return remember {
        SimpleBottomSheetScaffoldState(
            topContentSize, appBarSize, scaffoldState
        )
    }
}

@Preview
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SimpleBottomSheetScaffold(
    simpleBottomSheetScaffoldState: SimpleBottomSheetScaffoldState = rememberSimpleBottomSheetScaffoldState(),
    topContent: @Composable () -> Unit = {},
    sheetContent: @Composable () -> Unit = {},
    fab: @Composable (() -> Unit)? = null,
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

    val dragAlpha = simpleBottomSheetScaffoldState.dragProgress.floatValue.ratioBetween(
        start = 0.2f,
        end = 0.5f,
    ).coerceIn(0f, 1f)
        .accelerateDecelerate()


    CompositionLocalProvider(
        LocalOverscrollConfiguration.provides(null),
    ) {
        BottomSheetScaffold(
            scaffoldState = simpleBottomSheetScaffoldState.scaffoldState,
            sheetPeekHeight = sheetMinSize,
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

    if (fab != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 56.dp, end = 16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            fab.invoke()
        }
    }
}


@Composable
@Preview
fun AppLabelTitle(
    modifier: Modifier = Modifier,
    content: (@Composable () -> Unit)? = null
) {
    Box(
        modifier = modifier
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        if (content != null) {
            content.invoke()
        } else {
            Image(
                painter = painterResource(id = R.drawable.logo_big),
                contentDescription = stringResource(id = R.string.app_name),
                contentScale = ContentScale.Inside,
                modifier = Modifier.scale(0.5f)
            )
        }
    }
}




