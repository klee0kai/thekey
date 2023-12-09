package com.github.klee0kai.thekey.app.ui.designkit.components

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import com.github.klee0kai.thekey.app.utils.views.accelerateDecelerate
import com.github.klee0kai.thekey.app.utils.views.fadeOutInAnimate
import com.github.klee0kai.thekey.app.utils.views.ratioBetween

internal object SimpleScaffoldConst {
    val appBarSize = 64.dp // TopAppBarSmallTokens.ContainerHeight
    val dragHandleSize = 48.dp
}

@SuppressLint("CoroutineCreationDuringComposition")
@Preview
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SimpleBottomSheetScaffold(
    topContentSize: Dp = 190.dp,
    navIcon: ImageVector = Icons.Filled.ArrowBack,
    navClick: (() -> Unit)? = null,
    appBarSticky: (@Composable () -> Unit)? = null,
    topContent: @Composable ConstraintLayoutScope.() -> Unit = {},
    sheetContent: @Composable ConstraintLayoutScope.() -> Unit = {},
) {
    val colorScheme = DI.theme().colorScheme().androidColorScheme
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()

    val mainTitleVisibility = remember { mutableStateOf(true) }
    val mainTitleAlpha = remember { mutableFloatStateOf(1f) }
    val secondTitleAlpha = remember { mutableFloatStateOf(0f) }

    val viewHeight = with(LocalDensity.current) {
        if (LocalView.current.isInEditMode) 900.dp else LocalView.current.height.toDp()
    }
    val scaffoldTopOffset = runCatching {
        with(LocalDensity.current) { scaffoldState.bottomSheetState.requireOffset().toDp() }
    }.getOrElse { 0.dp }

    val sheetMaxSize = viewHeight - appBarSize
    val sheetMinSize = viewHeight - appBarSize - dragHandleSize - topContentSize

    when {
        appBarSticky != null && scaffoldTopOffset < appBarSize + 10.dp ->
            mainTitleVisibility.value = false

        scaffoldTopOffset > appBarSize + 30.dp -> mainTitleVisibility.value = true
    }

    LaunchedEffect(key1 = mainTitleVisibility.value) {
        fadeOutInAnimate(
            reverse = mainTitleVisibility.value,
            alpha1Init = mainTitleAlpha.value,
            alpha2Init = secondTitleAlpha.value,
        ) { newMainTitleAlpha, newSecondTitleAlpha ->
            mainTitleAlpha.value = newMainTitleAlpha
            secondTitleAlpha.value = newSecondTitleAlpha
        }
    }

    val dragAlpha = scaffoldTopOffset.ratioBetween(
        start = appBarSize + dragHandleSize * 0.5f,
        end = appBarSize + dragHandleSize + 20.dp,
    ).coerceIn(0f, 1f)
        .accelerateDecelerate()



    CompositionLocalProvider(
        LocalOverscrollConfiguration.provides(null),
    ) {
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = sheetMinSize,
            contentColor = colorScheme.onBackground,
            containerColor = colorScheme.background,
            content = { innerPadding ->
                ConstraintLayout(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxWidth()
                        .height(scaffoldTopOffset),
                    content = {
                        topContent.invoke(this)
                    }
                )
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
                            .background(colorScheme.onSurface.copy(alpha = 0.4f * dragAlpha))
                    )
                }
            },
            sheetContainerColor = colorScheme.surface,
            sheetContentColor = colorScheme.onSurface,
            sheetContent = {
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(sheetMaxSize),
                    content = {
                        sheetContent.invoke(this)
                    }
                )
            }
        )
    }

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorScheme.background,
        ),
        title = {
            if (mainTitleAlpha.value > 0) {
                AppLabelTitle(modifier = Modifier.alpha(mainTitleAlpha.value))
            } else {
                AppLabelTitle(
                    modifier = Modifier.alpha(secondTitleAlpha.value),
                    content = appBarSticky
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




