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
    val colorScheme = DI.theme().colorScheme().androidColorScheme
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()

    val viewHeight = with(LocalDensity.current) {
        if (LocalView.current.isInEditMode) 600.dp else LocalView.current.height.toDp()
    }
    val scaffoldTopOffset = runCatching {
        with(LocalDensity.current) { scaffoldState.bottomSheetState.requireOffset().toDp() }
    }.getOrElse { 0.dp }

    val sheetMaxSize = viewHeight - dragHandleSize
    val sheetMinSize = viewHeight - appBarSize - dragHandleSize - topContentSize

    val dragAlpha = ratio(
        start = appBarSize + dragHandleSize * 0.5f,
        end = appBarSize + dragHandleSize + 20.dp,
        point = scaffoldTopOffset
    ).coerceIn(0f, 1f)

    CompositionLocalProvider(
        LocalOverscrollConfiguration.provides(null),
    ) {
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = sheetMinSize,
            content = { innerPadding ->
                ConstraintLayout(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxWidth()
                        .height(scaffoldTopOffset)
                        .background(colorScheme.background),
                    content = {
                        topContent.invoke(this)

                    }
                )
            },
            sheetShape = BottomSheetDefaults.HiddenShape,
            sheetDragHandle = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dragHandleSize)
                        .background(colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 32.dp, height = 4.dp)
                            .background(colorScheme.onSurface.copy(alpha = 0.4f * dragAlpha))
                    )
                }
            },
            sheetContent = {
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(sheetMaxSize)
                        .background(colorScheme.surface),
                    content = { sheetContent.invoke(this) }
                )
            }
        )
    }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorScheme.background,
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

}


private fun ratio(start: Dp, end: Dp, point: Dp): Float {
    val len = end - start
    val passed = point - start
    return passed / len
}