package com.github.klee0kai.thekey.app.ui.simpleboard.components

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
import com.github.klee0kai.thekey.app.ui.simpleboard.components.popup.OpenedStoragePopupMenu
import com.github.klee0kai.thekey.app.ui.simpleboard.presenter.SimpleBoardPresenterDummy
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
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
    val theme = LocalTheme.current
    val presenter by rememberOnScreenRef { DI.simpleBoardPresenter() }
    val opened by presenter!!.openedStoragesFlow.collectAsState(key = Unit, initial = emptyList())
    val favorites by presenter!!.favoritesStorages.collectAsState(key = Unit, initial = emptyList())

    LazyColumn(
        modifier = modifier
            .animateContentSize()
    ) {
        item("header") { header() }
        if (opened.isNotEmpty()) {
            item("openned_header") {
                Text(
                    text = stringResource(id = R.string.openned),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 30.dp, bottom = 4.dp),
                    style = theme.typeScheme.header,
                    color = theme.colorScheme.textColors.hintTextColor,
                )
            }
        }

        opened.forEach { storage ->
            item(key = "openned-${storage.path}") {
                var showMenu by remember { mutableStateOf(false) }
                val position = rememberViewPosition()

                FavoriteStorageElement(
                    modifier = Modifier
                        .onGlobalPositionState(position),
                    storage = storage,
                    onClick = rememberClickDebounced(storage.path) {
                        showMenu = false
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
                        modifier = Modifier.padding(vertical = 4.dp),
                        onLogout = rememberClickDebounced(storage.path) {
                            showMenu = false
                            presenter?.logout(storage.path, router)
                        }
                    )
                }
            }
        }

        if (favorites.isNotEmpty()) {
            item("favorites_header") {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 30.dp, bottom = 4.dp),
                    text = stringResource(id = R.string.favorites),
                    style = theme.typeScheme.header,
                    color = theme.colorScheme.textColors.hintTextColor,
                )
            }
        }

        favorites.forEach { storage ->
            item(key = "favorites-${storage.path}") {
                FavoriteStorageElement(
                    storage = storage,
                    onClick = rememberClickDebounced(storage.path) {
                        presenter?.openStorage(storage.path, router)
                    },
                )
            }
        }

        item("footer") { footer() }
    }

}

@OptIn(DebugOnly::class)
@Preview
@Composable
private fun NavigationMapListPreview() = AppTheme(theme = DefaultThemes.darkTheme) {
    DI.hardResetToPreview()
    DI.initPresenterModule(object : PresentersModule {
        override fun simpleBoardPresenter() = SimpleBoardPresenterDummy(
        )
    })
    StorageNavigationMapList()
}
