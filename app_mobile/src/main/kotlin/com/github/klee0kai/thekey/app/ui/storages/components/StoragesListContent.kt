package com.github.klee0kai.thekey.app.ui.storages.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.hardResetToPreview
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.ui.storages.components.popup.StoragePopupMenu
import com.github.klee0kai.thekey.app.ui.storages.presenter.StoragesPresenterDummy
import com.github.klee0kai.thekey.core.domain.model.ColoredStorage
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.overlay.PopupMenu
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.possitions.onGlobalPositionState
import com.github.klee0kai.thekey.core.utils.possitions.rememberViewPosition
import com.github.klee0kai.thekey.core.utils.views.DebugDarkContentPreview
import com.github.klee0kai.thekey.core.utils.views.collectAsState
import com.github.klee0kai.thekey.core.utils.views.rememberDerivedStateOf
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef

@Composable
fun StoragesListContent(
    modifier: Modifier = Modifier,
    onExport: ((ColoredStorage) -> Unit)? = null,
    onEdit: ((ColoredStorage) -> Unit)? = null,
    header: @Composable LazyItemScope.() -> Unit = { },
    footer: @Composable LazyItemScope.() -> Unit = {},
) {
    val navigator = LocalRouter.current
    val theme = LocalTheme.current
    val scope = rememberCoroutineScope()
    val presenter by rememberOnScreenRef { DI.storagesPresenter() }
    val storages = presenter!!.filteredStorages.collectAsState(key = Unit, initial = emptyList())
    val hasActions by rememberDerivedStateOf { listOf(onExport, onEdit).any { it != null } }

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
                var showMenu by remember { mutableStateOf(false) }
                val position = rememberViewPosition()

                ColoredStorageItem(
                    modifier = Modifier
                        .onGlobalPositionState(position),
                    storage = storage,
                    onClick = { navigator.backWithResult(storage.path) },
                    onLongClick = { showMenu = true },
                )

                PopupMenu(
                    visible = showMenu,
                    positionAnchor = position,
                    horizontalBias = 0.8f,
                    onDismissRequest = { showMenu = false }
                ) {
                    StoragePopupMenu(
                        modifier = Modifier.padding(vertical = 4.dp),
                        onExport = onExport?.let { { onExport.invoke(storage) } },
                        onEdit = onEdit?.let { { onEdit.invoke(storage) } },
                    )
                }
            }
        }

        item { footer() }
    }
}


@OptIn(DebugOnly::class)
@Composable
@Preview
fun StoragesListContentPreview() = DebugDarkContentPreview {
    DI.hardResetToPreview()
    DI.initPresenterModule(object : PresentersModule {
        override fun storagesPresenter() = StoragesPresenterDummy()
    })

    StoragesListContent(
        onExport = {},
        onEdit = {},
    )
}
