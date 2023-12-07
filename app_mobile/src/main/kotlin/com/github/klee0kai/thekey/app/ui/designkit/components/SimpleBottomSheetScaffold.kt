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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintLayoutScope
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.designkit.components.SimpleScaffoldConst.appBarSize
import com.github.klee0kai.thekey.app.ui.designkit.components.SimpleScaffoldConst.dragHandleSize

internal object SimpleScaffoldConst {
    val appBarSize = 64.dp // TopAppBarSmallTokens.ContainerHeight
    val dragHandleSize = 48.dp
}

@Preview
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SimpleBottomSheetScaffold(
    title: String = stringResource(R.string.app_name),
    topContentSize: Dp = 190.dp,
    navIcon: ImageVector = Icons.Filled.ArrowBack,
    navClick: (() -> Unit)? = null,
    topContent: @Composable ConstraintLayoutScope.() -> Unit = {},
    sheetContent: @Composable ConstraintLayoutScope.() -> Unit = {},
) {
    val colorScheme = DI.theme().colorScheme()
    val scope = rememberCoroutineScope()

    val scaffoldState = rememberBottomSheetScaffoldState()
    val sheetMaxSize = with(LocalDensity.current) {
        LocalView.current.height.toDp() - appBarSize - dragHandleSize
    }
    val sheetMinSize = with(LocalDensity.current) {
        LocalView.current.height.toDp() - appBarSize - topContentSize
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
            scaffoldState = scaffoldState,
            sheetPeekHeight = sheetMinSize,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colorScheme.primaryBackground,
                    ),
                    title = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo_big),
                                contentDescription = stringResource(id = R.string.app_name),
                                contentScale = ContentScale.Inside,
                                modifier = Modifier.scale(0.5f)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { }) {
                            Icon(
                                Icons.Filled.Menu,
                                contentDescription = null,
                                modifier = Modifier
                            )
                        }
                    },
                )
            },
            sheetShape = BottomSheetDefaults.HiddenShape,
            sheetDragHandle = {
                val alfa = when {
                    scaffoldSize < 56.dp + 40.dp -> maxOf(
                        0f,
                        (scaffoldSize.value - 40.dp.value) / (56.dp.value + 40.dp.value)
                    )

                    else -> 1f
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dragHandleSize)
                        .background(colorScheme.secondBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(
                                width = 32.dp,
                                height = 4.dp
                            )
                            .background(colorScheme.secondElement.copy(alpha = 0.4f))
                    )
                }
            },
            content = { innerPadding ->
                ConstraintLayout(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxWidth()
                        .height(scaffoldSize)
                        .background(colorScheme.primaryBackground),
                    content = { topContent.invoke(this) }
                )
            },
            sheetContent = {
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(sheetMaxSize)
                        .background(colorScheme.secondBackground),
                    content = { sheetContent.invoke(this) }
                )
            }
        )
    }
}
