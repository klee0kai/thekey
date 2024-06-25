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
import com.github.klee0kai.thekey.core.utils.views.collectAsState
import com.github.klee0kai.thekey.core.utils.views.currentRef
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef
import com.github.klee0kai.thekey.dynamic.findstorage.di.FSDI
import com.github.klee0kai.thekey.dynamic.findstorage.ui.storages.content.FSColoredStorageItem


@Composable
fun FSColoredStorageItemWidget(
    modifier: Modifier = Modifier,
    state: StorageItemWidgetState = StorageItemWidgetState(),
) {
    val router by LocalRouter.currentRef
    var showMenu by remember { mutableStateOf(false) }
    val position = rememberViewPosition()
    val presenter by rememberOnScreenRef { FSDI.fsStoragesPresenter() }
    val groups by presenter!!.selectableColorGroups.collectAsState(key = Unit, initial = emptyList())

    FSColoredStorageItem(
        modifier = modifier
            .onGlobalPositionState(position),
        storage = state.coloredStorage,
        onClick = state.onClick,
        onLongClick = { showMenu = true },
        icon = state.iconContent,
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
                colorGroups = groups,
                selectedGroupId = state.coloredStorage.colorGroup?.id ?: -1,
                onEdit = {
                    showMenu = false
                    presenter?.editStorage(state.coloredStorage.path, router)
                },
                onExport = {
                    showMenu = false
                    presenter?.exportStorage(state.coloredStorage.path, router)
                },
                onColorGroupSelected = {
                    showMenu = false
                    presenter?.setColorGroup(state.coloredStorage.path, it.id)
                }
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