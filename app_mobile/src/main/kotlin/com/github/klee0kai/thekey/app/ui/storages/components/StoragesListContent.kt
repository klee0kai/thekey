package com.github.klee0kai.thekey.app.ui.storages.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.hardResetToPreview
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.ui.storages.presenter.StoragesPresenterDummy
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.views.collectAsState
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef


@Composable
fun StoragesListContent(
    modifier: Modifier = Modifier,
    header: @Composable LazyItemScope.() -> Unit = { },
    footer: @Composable LazyItemScope.() -> Unit = {},
) {
    val navigator = LocalRouter.current
    val scope = rememberCoroutineScope()
    val presenter by rememberOnScreenRef { DI.storagesPresenter() }
    val storages = presenter!!.filteredStorages.collectAsState(key = Unit, initial = emptyList())

    if (storages.value.isEmpty()) {
        // show empty state if need

        // should return here so as not to reset the state of the list
        return
    }
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        state = rememberLazyListState(),
    ) {
        item { header() }

        storages.value.forEach { storage ->
            item(contentType = storage::class) {
                ColoredStorageItem(
                    storage = storage,
                    onClick = { navigator.backWithResult(storage.path) }
                )
            }
        }

        item { footer() }
    }
}


@OptIn(DebugOnly::class)
@Composable
@Preview
fun StoragesListContentPreview() {
    DI.hardResetToPreview()
    DI.initPresenterModule(object : PresentersModule {
        override fun storagesPresenter() = StoragesPresenterDummy()
    })

    AppTheme {
        StoragesListContent()
    }
}
