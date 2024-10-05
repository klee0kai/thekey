@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.github.klee0kai.thekey.core.ui.devkit.components.appbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.theme.DefaultThemes
import com.github.klee0kai.thekey.core.utils.views.animateAlphaAsState
import com.github.klee0kai.thekey.core.utils.views.animateTargetCrossFaded
import com.github.klee0kai.thekey.core.utils.views.rememberDerivedStateOf
import org.jetbrains.annotations.VisibleForTesting

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
    val theme = LocalTheme.current
    val animateTargetAlpha by animateTargetCrossFaded(target = titleId.value)
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
            CompositionLocalProvider(
                LocalTextStyle provides theme.typeScheme.screenHeader,
            ) {
                Box(
                    modifier = modifier
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(modifier = Modifier.alpha(animateTargetAlpha.alpha)) {
                        titleContent?.invoke(animateTargetAlpha.current)
                    }
                }
            }
        },
        navigationIcon = navigationIcon ?: {},
    )
}

@VisibleForTesting
@Composable
@Preview
fun AppBarTitlePreview1() = AppTheme(theme = DefaultThemes.darkTheme) {
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