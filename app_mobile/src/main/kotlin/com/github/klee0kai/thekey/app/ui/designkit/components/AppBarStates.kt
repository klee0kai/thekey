package com.github.klee0kai.thekey.app.ui.designkit.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
    modifier: Modifier = Modifier,
    titleId: Int = 0,
    navigationIcon: (@Composable () -> Unit)? = null,
    titleContent: (@Composable (titleId: Int) -> Unit)? = null,
) {
    val colorScheme = remember { DI.theme().colorScheme().androidColorScheme }

    var prevTitleId by remember { mutableStateOf(titleId) }
    var targetTitleId by remember { mutableStateOf(titleId) }

    val prevTitleAlpha = remember { mutableFloatStateOf(1f) }
    val targetTitleAlpha = remember { mutableFloatStateOf(0f) }

    LaunchedEffect(key1 = titleId) {
        if (targetTitleAlpha.floatValue == 0f) {
            targetTitleId = titleId
            targetTitleAlpha.floatValue = 0f
        } else {
            prevTitleId = targetTitleId
            targetTitleId = titleId
            prevTitleAlpha.floatValue = 1f
            targetTitleAlpha.floatValue = 0f
        }

        fadeOutInAnimate(
            alpha1Init = prevTitleAlpha.floatValue,
            alpha2Init = targetTitleAlpha.floatValue,
        ) { newPrevAlpha, newTargetAlpha ->
            prevTitleAlpha.floatValue = newPrevAlpha
            targetTitleAlpha.floatValue = newTargetAlpha
        }
    }

    CenterAlignedTopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorScheme.background,
        ),
        title = {
            Box(
                modifier = modifier
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {

                if (prevTitleAlpha.floatValue > 0f) {
                    Box(modifier = Modifier.alpha(prevTitleAlpha.floatValue)) {
                        titleContent?.invoke(prevTitleId)
                    }
                }

                Box(
                    modifier = modifier.alpha(targetTitleAlpha.floatValue),
                ) {
                    titleContent?.invoke(targetTitleId)
                }
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