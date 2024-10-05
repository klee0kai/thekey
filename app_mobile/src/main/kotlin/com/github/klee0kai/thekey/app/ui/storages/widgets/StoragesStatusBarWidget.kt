package com.github.klee0kai.thekey.app.ui.storages.widgets

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppTitleImage
import com.github.klee0kai.thekey.core.ui.devkit.theme.DefaultThemes
import com.github.klee0kai.thekey.core.ui.navigation.model.StoragesStatusBarWidgetState
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.views.animateTargetFaded

@Composable
fun StoragesStatusBarWidget(
    modifier: Modifier = Modifier,
    state: StoragesStatusBarWidgetState = StoragesStatusBarWidgetState(),
    parent: @Composable (Modifier, StoragesStatusBarWidgetState) -> Unit = { _, _ -> },
) {
    val expandedAnimated by animateTargetFaded(target = state.isContentExpanded)

    when {
        expandedAnimated.current == true -> {
            Text(
                modifier = modifier.alpha(expandedAnimated.alpha),
                text = stringResource(id = R.string.storages),
            )
        }

        else -> {
            AppTitleImage(
                modifier = modifier.alpha(expandedAnimated.alpha),
            )
        }
    }
}


@Composable
@DebugOnly
@Preview
fun StoragesStatusBarWidgetCollapsedPreview() = AppTheme(theme = DefaultThemes.darkTheme) {
    StoragesStatusBarWidget(
        state = StoragesStatusBarWidgetState(
            isContentExpanded = false,
        )
    )
}

@Composable
@DebugOnly
@Preview
fun StoragesStatusBarWidgetExpandedPreview() = AppTheme(theme = DefaultThemes.darkTheme) {
    StoragesStatusBarWidget(
        state = StoragesStatusBarWidgetState(
            isContentExpanded = true,
        )
    )
}

