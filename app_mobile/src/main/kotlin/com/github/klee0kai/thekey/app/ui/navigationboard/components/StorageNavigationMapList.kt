package com.github.klee0kai.thekey.app.ui.navigationboard.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.hardResetToPreview
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.ui.navigationboard.components.popup.OpenedStoragePopupMenu
import com.github.klee0kai.thekey.app.ui.navigationboard.presenter.NavigationBoardPresenterDummy
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.overlay.PopupMenu
import com.github.klee0kai.thekey.core.ui.devkit.theme.DefaultThemes
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.possitions.onGlobalPositionState
import com.github.klee0kai.thekey.core.utils.possitions.rememberViewPosition
import com.github.klee0kai.thekey.core.utils.views.collectAsState
import com.github.klee0kai.thekey.core.utils.views.currentRef
import com.github.klee0kai.thekey.core.utils.views.rememberClickDebounced
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef

@Composable
fun StorageNavigationMapList(
    modifier: Modifier = Modifier,
    header: @Composable () -> Unit = {},
    footer: @Composable () -> Unit = {},
) {
    val router by LocalRouter.currentRef
    val presenter by rememberOnScreenRef { DI.navigationBoardPresenter() }
    val opened by presenter!!.openedStoragesFlow.collectAsState(key = Unit, initial = emptyList())
    val favorites by presenter!!.favoritesStorages.collectAsState(key = Unit, initial = emptyList())

    LazyColumn(
        modifier = modifier
            .animateContentSize()
    ) {
        item { header() }
        if (opened.isNotEmpty()) {
            item {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 30.dp, bottom = 4.dp)
                        .alpha(0.4f),
                    text = stringResource(id = R.string.openned)
                )
            }
        }

        opened.forEach { storage ->
            item {
                var showMenu by remember { mutableStateOf(false) }
                val position = rememberViewPosition()

                FavoriteStorageItem(
                    modifier = modifier
                        .onGlobalPositionState(position),
                    storage = storage,
                    onClick = rememberClickDebounced {
                        presenter?.openStorage(storage.path, router)
                    },
                    onLongClick = rememberClickDebounced { showMenu = true },
                )
                PopupMenu(
                    visible = showMenu,
                    positionAnchor = position,
                    horizontalBias = 0.8f,
                    onDismissRequest = rememberClickDebounced { showMenu = false }
                ) {
                    OpenedStoragePopupMenu(
                        onLogout = rememberClickDebounced {
                            showMenu = false
                            presenter?.logout(storage.path, router)
                        }
                    )
                }
            }
        }

        if (favorites.isNotEmpty()) {
            item {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 30.dp, bottom = 4.dp)
                        .alpha(0.4f),
                    text = stringResource(id = R.string.favorites)
                )
            }
        }

        favorites.forEach { storage ->
            item {
                FavoriteStorageItem(
                    storage = storage,
                    onClick = rememberClickDebounced {
                        presenter?.openStorage(storage.path, router)
                    },
                )
            }
        }

        item { footer() }
    }

}


@OptIn(DebugOnly::class)
@Preview
@Composable
private fun NavigationMapListPreview() = AppTheme(theme = DefaultThemes.darkTheme) {
    DI.hardResetToPreview()
    DI.initPresenterModule(object : PresentersModule {
        override fun navigationBoardPresenter() = NavigationBoardPresenterDummy(
        )
    })
    StorageNavigationMapList()
}
