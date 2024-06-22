package com.github.klee0kai.thekey.dynamic.findstorage.ui.storages.widgets

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.hardResetToPreview
import com.github.klee0kai.thekey.app.ui.storages.components.popup.StoragePopupMenu
import com.github.klee0kai.thekey.core.domain.model.ColoredStorage
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.overlay.PopupMenu
import com.github.klee0kai.thekey.core.ui.navigation.model.StorageItemWidgetState
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.possitions.onGlobalPositionState
import com.github.klee0kai.thekey.core.utils.possitions.rememberViewPosition
import com.github.klee0kai.thekey.core.utils.views.DebugDarkContentPreview
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef
import com.github.klee0kai.thekey.dynamic.findstorage.ui.storages.content.FSColoredStorageItem


@Composable
fun FSColoredStorageItemWidget(
    modifier: Modifier = Modifier,
    state: StorageItemWidgetState = StorageItemWidgetState(),
) {
    val router = LocalRouter.current
    var showMenu by remember { mutableStateOf(false) }
    val position = rememberViewPosition()
    val presenter by rememberOnScreenRef { DI.storagesPresenter() }

    FSColoredStorageItem(
        modifier = modifier
            .onGlobalPositionState(position),
        storage = state.coloredStorage,
        onClick = { router.backWithResult(state.coloredStorage.path) },
        onLongClick = { showMenu = true },
    )

    if (state.isPopupMenuAvailable) {
        PopupMenu(
            visible = showMenu,
            positionAnchor = position,
            horizontalBias = 0.8f,
            onDismissRequest = { showMenu = false }
        ) {
            StoragePopupMenu(
                modifier = Modifier.padding(vertical = 4.dp),
                onEdit = { presenter?.editStorage(state.coloredStorage.path, router) },
                onExport = { presenter?.exportStorage(state.coloredStorage.path, router) },
            )
        }
    }
}


@OptIn(DebugOnly::class)
@Preview
@Composable
fun FSColoredStorageSimpleItemPreview() = DebugDarkContentPreview {
    DI.hardResetToPreview()
    FSColoredStorageItemWidget(
        state = StorageItemWidgetState(
            ColoredStorage(
                path = "path",
                name = "name",
                description = "description",
                version = 1,
            ),
        )
    )
}