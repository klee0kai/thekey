@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.github.klee0kai.thekey.app.ui.designkit.components.appbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.ui.designkit.AppTheme
import com.github.klee0kai.thekey.app.ui.designkit.components.bottomsheet.SimpleBottomSheetScaffoldState
import com.github.klee0kai.thekey.app.utils.views.animateAlphaAsState
import com.github.klee0kai.thekey.app.utils.views.animateTargetAlphaAsState
import com.github.klee0kai.thekey.app.utils.views.rememberDerivedStateOf

object AppBarConst {
    val appBarSize = 64.dp // TopAppBarSmallTokens.ContainerHeight
}


@Composable
fun AppBarStates(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    titleId: State<Int> = remember { mutableIntStateOf(0) },
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    titleContent: (@Composable (titleId: Int) -> Unit)? = null,
) {
    val animateTargetAlpha by animateTargetAlphaAsState(target = titleId.value)
    val appBarAlpha by animateAlphaAsState(isVisible)
    val isNotVisible by rememberDerivedStateOf { appBarAlpha <= 0 }

    if (isNotVisible) return
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
                    titleContent?.invoke(animateTargetAlpha.current)
                }
            }
        },
        navigationIcon = navigationIcon ?: {},
    )
}

@Composable
@NonRestartableComposable
fun SimpleBottomSheetScaffoldState.rememberMainTitleVisibleFlow(
    hideTitleOffset: Dp = 10.dp,
    showTitleOffset: Dp = 30.dp,
): State<Boolean?> {
    val mainTitleVisibility = remember { mutableStateOf<Boolean?>(null) }
    val scaffoldTopOffset = runCatching {
        with(LocalDensity.current) {
            scaffoldState.bottomSheetState.requireOffset().toDp()
        }
    }.getOrNull()

    when {
        scaffoldTopOffset == null -> mainTitleVisibility.value = null
        scaffoldTopOffset < appBarSize + hideTitleOffset -> mainTitleVisibility.value = false
        scaffoldTopOffset > appBarSize + showTitleOffset -> mainTitleVisibility.value = true
    }
    return mainTitleVisibility
}


@Composable
@Preview
private fun AppBarTitlePreview() = AppTheme {
    AppBarStates(
        navigationIcon = {
            IconButton(onClick = { }) {
                Icon(Icons.Filled.Menu, contentDescription = null)
            }
        }
    ) {
        Text("Title")
    }
}