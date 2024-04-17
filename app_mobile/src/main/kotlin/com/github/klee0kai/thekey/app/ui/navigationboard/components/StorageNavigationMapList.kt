package com.github.klee0kai.thekey.app.ui.navigationboard.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.ui.designkit.AppTheme
import com.github.klee0kai.thekey.app.ui.navigationboard.presenter.NavigationBoardPresenterDummy
import com.github.klee0kai.thekey.app.utils.views.collectAsState

@Composable
fun StorageNavigationMapList(
    modifier: Modifier = Modifier,
) {
    val presenter = remember { DI.navigationBoardPresenter() }
    val opened by presenter.openedStoragesFlow.collectAsState(key = Unit, initial = emptyList())
    val favorites by presenter.favoritesStorages.collectAsState(key = Unit, initial = emptyList())

    LazyColumn(
        modifier = modifier
            .animateContentSize()
    ) {
        if (opened.isNotEmpty()) {
            item {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 30.dp, bottom = 4.dp),
                    text = stringResource(id = R.string.openned)
                )
            }
        }

        opened.forEach { storage ->
            item {
                FavoriteStorageItem(
                    storage = storage
                )
            }
        }

        if (favorites.isNotEmpty()) {
            item {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 30.dp, bottom = 4.dp),
                    text = stringResource(id = R.string.favorites)
                )
            }
        }

        favorites.forEach { storage ->
            item {
                FavoriteStorageItem(
                    storage = storage
                )
            }
        }
    }

}


@Preview
@Composable
private fun NavigationMapListPreview() = AppTheme {
    DI.initPresenterModule(object : PresentersModule() {
        override fun navigationBoardPresenter() = NavigationBoardPresenterDummy(
            hasOpened = true,
            hasFavorites = true,
        )
    })
    StorageNavigationMapList()
}