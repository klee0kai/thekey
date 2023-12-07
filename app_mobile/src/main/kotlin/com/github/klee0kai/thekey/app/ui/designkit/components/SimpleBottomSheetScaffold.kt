package com.github.klee0kai.thekey.app.ui.designkit.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintLayoutScope


@Preview
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SimpleBottomSheetScaffold(
    topContentSize: Dp = 190.dp,
    topContent: @Composable ConstraintLayoutScope.() -> Unit = {},
    sheetContent: @Composable ConstraintLayoutScope.() -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val sheetMaxSize = with(LocalDensity.current) {
        LocalView.current.height.toDp() - 56.dp - 48.dp
    }
    val sheetMinSize = with(LocalDensity.current) {
        LocalView.current.height.toDp() - 56.dp - topContentSize
    }
    val scaffoldSize = runCatching {
        with(LocalDensity.current) {
            scaffoldState.bottomSheetState.requireOffset().toDp()
        }
    }.getOrElse { 0.dp }

    CompositionLocalProvider(
        LocalOverscrollConfiguration.provides(null),

    ) {
        BottomSheetScaffold(
            topBar = {
                SimpleAppBar(
                    navIcon = Icons.Filled.Menu,
                    navClick = { })
            },
            scaffoldState = scaffoldState,
            sheetPeekHeight = sheetMinSize,
            sheetShape = BottomSheetDefaults.HiddenShape,
            sheetDragHandle = {
                val alfa = when {
                    scaffoldSize < 56.dp + 40.dp -> maxOf(
                        0f,
                        (scaffoldSize.value - 40.dp.value) / (56.dp.value + 40.dp.value)
                    )

                    else -> 1f
                }

                Surface(
                    modifier = Modifier
                        .padding(22.dp)
                        .background(
                            MaterialTheme.colorScheme.background
                                .copy(alpha = alfa)

                        ),
                    color = MaterialTheme.colorScheme.primary
                        .copy(alpha = alfa),
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Box(
                        Modifier
                            .size(
                                width = 32.dp,
                                height = 4.dp
                            )
                    )
                }
            },
            content = { innerPadding ->
                ConstraintLayout(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxWidth()
                        .height(scaffoldSize)
                ) {
                    topContent.invoke(this)
                }
            },
            sheetContent = {
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(sheetMaxSize)
                ) {
                    sheetContent.invoke(this)
                }
            },

            )
    }
}
