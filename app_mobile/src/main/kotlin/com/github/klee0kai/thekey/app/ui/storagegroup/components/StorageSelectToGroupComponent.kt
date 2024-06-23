@file:OptIn(ExperimentalFoundationApi::class)

package com.github.klee0kai.thekey.app.ui.storagegroup.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.hardResetToPreview
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.ui.navigation.identifier
import com.github.klee0kai.thekey.app.ui.navigation.model.EditStorageGroupDestination
import com.github.klee0kai.thekey.app.ui.storagegroup.model.toColorStorage
import com.github.klee0kai.thekey.app.ui.storagegroup.presenter.EditStoragesGroupPresenterDummy
import com.github.klee0kai.thekey.core.di.identifiers.StorageGroupIdentifier
import com.github.klee0kai.thekey.core.ui.devkit.LocalScreenResolver
import com.github.klee0kai.thekey.core.ui.navigation.model.StorageItemWidgetState
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.common.Dummy
import com.github.klee0kai.thekey.core.utils.views.DebugDarkContentPreview
import com.github.klee0kai.thekey.core.utils.views.animateAlphaAsState
import com.github.klee0kai.thekey.core.utils.views.animateContentSizeProduction
import com.github.klee0kai.thekey.core.utils.views.animateTargetCrossFaded
import com.github.klee0kai.thekey.core.utils.views.collectAsState
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef
import org.jetbrains.annotations.VisibleForTesting


@Composable
fun StorageSelectToGroupComponent(
    modifier: Modifier = Modifier,
    dest: EditStorageGroupDestination = EditStorageGroupDestination(),
    isSelectAvailable: Boolean = false,
    onSelect: (storagePath: String, selected: Boolean) -> Unit = { _, _ -> },
    header: @Composable LazyItemScope.() -> Unit = { Spacer(modifier = Modifier.height(12.dp)) },
    footer: @Composable LazyItemScope.() -> Unit = { Spacer(modifier = Modifier.height(12.dp)) },
) {
    val screenResolver = LocalScreenResolver.current
    val presenter by rememberOnScreenRef { DI.editStorageGroupPresenter(dest.identifier()) }
    val storages by presenter!!.allStorages.collectAsState(key = Unit, initial = emptyList())
    val selectAvailableAlpha by animateAlphaAsState(isSelectAvailable)

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
                val animatedStorage by animateTargetCrossFaded(storage)
                val icon by animateTargetCrossFaded(target = if (storage.selected) Icons.Default.Check else Icons.Filled.Add)

                screenResolver.widget(
                    modifier = Modifier.animateContentSizeProduction(),
                    widgetState = StorageItemWidgetState(
                        coloredStorage = storage.toColorStorage(),
                        onClick = {
                            if (isSelectAvailable) {
                                onSelect.invoke(storage.path, !storage.selected)
                            }
                        },
                        iconContent = {
                            Icon(
                                modifier = Modifier
                                    .alpha(icon.alpha)
                                    .alpha(selectAvailableAlpha)
                                    .alpha(animatedStorage.alpha),
                                imageVector = icon.current,
                                contentDescription = "Added"
                            )
                        },
                    )
                )

            }
        }
        item { footer() }
    }
}

@OptIn(DebugOnly::class)
@VisibleForTesting
@Preview
@Composable
fun NoteSelectToGroupComponentPreview() {
    DI.hardResetToPreview()
    DI.initPresenterModule(object : PresentersModule {
        override fun editStorageGroupPresenter(storageIdentifier: StorageGroupIdentifier) = object : EditStoragesGroupPresenterDummy() {
        }
    })

    DebugDarkContentPreview {
        StorageSelectToGroupComponent(dest = EditStorageGroupDestination(groupId = Dummy.dummyId))
    }
}