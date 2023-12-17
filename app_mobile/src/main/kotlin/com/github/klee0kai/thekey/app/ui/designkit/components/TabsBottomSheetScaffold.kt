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
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.designkit.components.SimpleScaffoldConst.appBarSize
import com.github.klee0kai.thekey.app.ui.designkit.components.SimpleScaffoldConst.dragHandleSize
import com.github.klee0kai.thekey.app.utils.views.accelerateDecelerate
import com.github.klee0kai.thekey.app.utils.views.fadeOutInAnimate
import com.github.klee0kai.thekey.app.utils.views.ratioBetween

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
class TabsBottomSheetScaffoldState(
    val titles: List<String> = emptyList(),
    val scaffoldState: BottomSheetScaffoldState,
    val dragProgress: MutableFloatState = mutableFloatStateOf(0f),
    val pagerState: PagerState,
)


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun rememberTabsBottomSheetScaffoldState(
    titles: List<String> = remember { listOf() },
    scaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState(),
): TabsBottomSheetScaffoldState {
    val pagerState = rememberPagerState { titles.size }
    return remember {
        TabsBottomSheetScaffoldState(
            titles = titles,
            scaffoldState = scaffoldState,
            pagerState = pagerState,
        )
    }
}

@Preview
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TabsBottomSheetScaffold(
    scaffoldState: TabsBottomSheetScaffoldState = rememberTabsBottomSheetScaffoldState(),
    topContentSize: Dp = 190.dp,
    navigationIcon: (@Composable () -> Unit)? = null,
    appBarSticky: (@Composable () -> Unit)? = null,
    topContent: @Composable () -> Unit = {},
    sheetContent: @Composable () -> Unit = {},
    fab: @Composable (() -> Unit)? = null,
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val view = LocalView.current
    val colorScheme = remember { DI.theme().colorScheme().androidColorScheme }

    val mainTitleVisibility = remember { mutableStateOf(true) }
    val mainTitleAlpha = remember { mutableFloatStateOf(1f) }
    val secondTitleAlpha = remember { mutableFloatStateOf(0f) }
    val viewHeight = remember {
        with(density) { if (view.isInEditMode) 900.dp else view.height.toDp() }
    }
    val sheetMaxSize = remember { viewHeight - appBarSize }
    val sheetMinSize = remember { viewHeight - topContentSize }

    val scaffoldTopOffset = runCatching {
        with(LocalDensity.current) {
            scaffoldState.scaffoldState.bottomSheetState.requireOffset().toDp()
        }
    }.getOrElse { 0.dp }


    when {
        appBarSticky != null && scaffoldTopOffset < appBarSize + 10.dp ->
            mainTitleVisibility.value = false

        scaffoldTopOffset > appBarSize + 30.dp -> mainTitleVisibility.value = true
    }

    scaffoldState.dragProgress.floatValue = scaffoldTopOffset.ratioBetween(
        start = appBarSize,
        end = appBarSize + topContentSize
    ).coerceIn(0f, 1f)

    val dragAlpha = scaffoldState.dragProgress.floatValue.ratioBetween(
        start = 0.2f,
        end = 0.5f,
    ).coerceIn(0f, 1f)
        .accelerateDecelerate()


    LaunchedEffect(key1 = mainTitleVisibility.value) {
        fadeOutInAnimate(
            reverse = mainTitleVisibility.value,
            alpha1Init = mainTitleAlpha.floatValue,
            alpha2Init = secondTitleAlpha.floatValue,
        ) { newMainTitleAlpha, newSecondTitleAlpha ->
            mainTitleAlpha.floatValue = newMainTitleAlpha
            secondTitleAlpha.floatValue = newSecondTitleAlpha
        }
    }


    CompositionLocalProvider(
        LocalOverscrollConfiguration.provides(null),
    ) {
        BottomSheetScaffold(
            scaffoldState = scaffoldState.scaffoldState,
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

    LargeTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorScheme.background,
        ),
        title = {
            if (mainTitleAlpha.floatValue > 0) {
                AppLabelTitle(modifier = Modifier.alpha(mainTitleAlpha.floatValue))
            } else {
                AppLabelTitle(
                    modifier = Modifier.alpha(secondTitleAlpha.floatValue),
                    content = appBarSticky
                )
            }

//            SecondaryTabRow(
//                selectedTabIndex = scaffoldState.pagerState.currentPage,
//            ) {
//                scaffoldState.titles.forEachIndexed { index: Int, title: String ->
//                    val selected = scaffoldState.pagerState.currentPage == index
//
//                    Tab(
//                        selected = selected,
//                        onClick = {
//                            scope.launch {
//                                scaffoldState.pagerState.animateScrollToPage(index)
//                            }
//                        }
//                    ) {
//                        Column(
//                            Modifier
//                                .padding(10.dp)
//                                .height(30.dp)
//                                .fillMaxWidth(),
//                            verticalArrangement = Arrangement.SpaceBetween
//                        ) {
//                            Text(
//                                text = title,
//                                style = MaterialTheme.typography.bodyLarge,
//                                modifier = Modifier.align(Alignment.CenterHorizontally)
//                            )
//                        }
//                    }
//
//                }
//            }
        },
        navigationIcon = navigationIcon ?: {},
    )

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





