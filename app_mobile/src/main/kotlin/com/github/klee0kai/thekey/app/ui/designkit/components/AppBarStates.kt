package com.github.klee0kai.thekey.app.ui.designkit.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.utils.views.animateAlphaAsState
import com.github.klee0kai.thekey.app.utils.views.animateTargetAlphaAsState

object AppBarConst {
    val appBarSize = 64.dp // TopAppBarSmallTokens.ContainerHeight
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AppBarStates(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    titleId: Int = 0,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    titleContent: (@Composable (titleId: Int) -> Unit)? = null,
) {
    val animateTargetAlpha by animateTargetAlphaAsState(target = titleId)
    val appBarAlpha by animateAlphaAsState(isVisible)

    if (appBarAlpha > 0) {
        CenterAlignedTopAppBar(
            modifier = modifier
                .alpha(appBarAlpha),
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.onBackground,
            ),
            actions = actions,
            title = {
                Box(
                    modifier = modifier
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(modifier = Modifier.alpha(animateTargetAlpha.alpha)) {
                        titleContent?.invoke(animateTargetAlpha.target)
                    }
                }
            },
            navigationIcon = navigationIcon ?: {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleBottomSheetScaffoldState.rememberMainTitleVisibleFlow(
    hideTitleOffset: Dp = 10.dp,
    showTitleOffset: Dp = 30.dp,
): State<Boolean> {
    val mainTitleVisibility = remember { mutableStateOf(true) }
    val scaffoldTopOffset = runCatching {
        with(LocalDensity.current) {
            scaffoldState.bottomSheetState.requireOffset().toDp()
        }
    }.getOrElse { 0.dp }

    when {
        scaffoldTopOffset < appBarSize + hideTitleOffset -> mainTitleVisibility.value = false
        scaffoldTopOffset > appBarSize + showTitleOffset -> mainTitleVisibility.value = true
    }
    return mainTitleVisibility
}