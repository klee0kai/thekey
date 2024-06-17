@file:OptIn(ExperimentalFoundationApi::class)

package com.github.klee0kai.thekey.app.ui.storagegroup.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.ui.navigation.identifier
import com.github.klee0kai.thekey.app.ui.navigation.model.EditStorageGroupDestination
import com.github.klee0kai.thekey.app.ui.storagegroup.presenter.EditStoragesGroupPresenterDummy
import com.github.klee0kai.thekey.core.di.identifiers.StorageGroupIdentifier
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.theme.DefaultThemes
import com.github.klee0kai.thekey.core.utils.common.Dummy
import com.github.klee0kai.thekey.core.utils.views.collectAsState
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef
import org.jetbrains.annotations.VisibleForTesting


@Composable
fun StorageSelectToGroupComponent(
    modifier: Modifier = Modifier,
    dest: EditStorageGroupDestination = EditStorageGroupDestination(),
    onSelect: (storagePath: String, selected: Boolean) -> Unit = { _, _ -> },
    header: @Composable LazyItemScope.() -> Unit = { Spacer(modifier = Modifier.height(12.dp)) },
    footer: @Composable LazyItemScope.() -> Unit = { Spacer(modifier = Modifier.height(12.dp)) },
) {
    val presenter by rememberOnScreenRef { DI.editStorageGroupPresenter(dest.identifier()) }
    val storages by presenter!!.allStorages.collectAsState(key = Unit, initial = emptyList())

    if (storages.isEmpty()) {
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
    ) {
        item { header() }

        storages.forEach { storage ->
            item(contentType = storage::class, key = storage.path) {
                SelectedStorageItem(
                    modifier = Modifier
                        .animateItemPlacement(animationSpec = tween()),
                    storage = storage,
                    onSelected = { selected ->
                        onSelect.invoke(storage.path, selected)
                    }
                )
            }
        }
        item { footer() }
    }
}

@VisibleForTesting
@Preview
@Composable
fun NoteSelectToGroupComponentPreview() = AppTheme(theme = DefaultThemes.darkTheme) {
    DI.initPresenterModule(object : PresentersModule {
        override fun editStorageGroupPresenter(storageIdentifier: StorageGroupIdentifier) = object : EditStoragesGroupPresenterDummy() {
        }
    })
    StorageSelectToGroupComponent(dest = EditStorageGroupDestination(groupId = Dummy.dummyId))
}