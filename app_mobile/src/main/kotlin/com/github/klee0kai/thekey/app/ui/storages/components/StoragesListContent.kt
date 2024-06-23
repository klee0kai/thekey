package com.github.klee0kai.thekey.app.ui.storages.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.hardResetToPreview
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.ui.storages.presenter.StoragesPresenterDummy
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.LocalScreenResolver
import com.github.klee0kai.thekey.core.ui.navigation.model.StorageItemWidgetState
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.views.DebugDarkContentPreview
import com.github.klee0kai.thekey.core.utils.views.collectAsState
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef

@Composable
fun StoragesListContent(
    modifier: Modifier = Modifier,
    isPopupMenuAvailable: Boolean = false,
    header: @Composable LazyItemScope.() -> Unit = { },
    footer: @Composable LazyItemScope.() -> Unit = {},
) {
    val router = LocalRouter.current
    val resolver = LocalScreenResolver.current
    val presenter by rememberOnScreenRef { DI.storagesPresenter() }
    val storages by presenter!!.filteredStorages.collectAsState(key = Unit, initial = emptyList())
    val groups by presenter!!.filteredColorGroups.collectAsState(key = Unit, initial = emptyList())

    if (storages.isEmpty()) {
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

        storages.forEach { storage ->
            item(contentType = storage::class) {
                resolver.widget(
                    modifier = Modifier,
                    widgetState = StorageItemWidgetState(
                        coloredStorage = storage,
                        isPopupMenuAvailable = isPopupMenuAvailable,
                        onClick = { router.backWithResult(storage.path) }
                    )
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
        override fun storagesPresenter() = StoragesPresenterDummy(
            groupsCount = 8,
        )
    })

    DebugDarkContentPreview {
        StoragesListContent()
    }
}
