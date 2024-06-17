package com.github.klee0kai.thekey.dynamic.findstorage.ui.storages.widgets

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.theme.DefaultThemes
import com.github.klee0kai.thekey.core.ui.navigation.model.StoragesStatusBarWidgetState
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.views.collectAsStateCrossFaded
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef
import com.github.klee0kai.thekey.dynamic.findstorage.R
import com.github.klee0kai.thekey.dynamic.findstorage.di.FSDI


@Composable
fun FSStoragesStatusBarWidget(
    modifier: Modifier = Modifier,
    state: StoragesStatusBarWidgetState = StoragesStatusBarWidgetState(),
    parent: @Composable (modifier: Modifier, state: StoragesStatusBarWidgetState) -> Unit = { _, _ -> },
) {
    val presenter by rememberOnScreenRef { FSDI.fsStoragesPresenter() }
    val isStoragesSearching by presenter!!.isStoragesSearchingProgress.collectAsStateCrossFaded(key = Unit, initial = null)

    when {
        isStoragesSearching.current == true -> {
            Text(
                modifier = modifier.alpha(isStoragesSearching.alpha),
                text = stringResource(id = R.string.strorage_searching),
            )
        }

        else -> {
            parent(
                modifier = modifier.alpha(isStoragesSearching.alpha),
                state = state,
            )
        }
    }
}


@Composable
@DebugOnly
@Preview
fun FSStoragesStatusBarWidgetPreview() = AppTheme(theme = DefaultThemes.darkTheme) {
    FSStoragesStatusBarWidget(
        state = StoragesStatusBarWidgetState(
            isContentExpanded = false,
        )
    )
}

