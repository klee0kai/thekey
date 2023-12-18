package com.github.klee0kai.thekey.app.ui.designkit.components

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.utils.views.fadeOutInAnimate

object AppBarConst {
    val appBarSize = 64.dp // TopAppBarSmallTokens.ContainerHeight
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AppBarStates(
    mainTitleVisibility: State<Boolean> = remember { mutableStateOf(true) },
    navigationIcon: (@Composable () -> Unit)? = null,
    appBarSticky: (@Composable () -> Unit)? = null,
) {
    val colorScheme = remember { DI.theme().colorScheme().androidColorScheme }

    val mainTitleAlpha = remember { mutableFloatStateOf(1f) }
    val secondTitleAlpha = remember { mutableFloatStateOf(0f) }

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

    CenterAlignedTopAppBar(
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
        },
        navigationIcon = navigationIcon ?: {},
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleBottomSheetScaffoldState.rememberMainTitleVisibleFlow(): State<Boolean> {
    val mainTitleVisibility = remember { mutableStateOf(true) }
    val scaffoldTopOffset = runCatching {
        with(LocalDensity.current) {
            scaffoldState.bottomSheetState.requireOffset().toDp()
        }
    }.getOrElse { 0.dp }

    when {
        scaffoldTopOffset < appBarSize + 10.dp -> mainTitleVisibility.value = false
        scaffoldTopOffset > appBarSize + 30.dp -> mainTitleVisibility.value = true
    }
    return mainTitleVisibility
}