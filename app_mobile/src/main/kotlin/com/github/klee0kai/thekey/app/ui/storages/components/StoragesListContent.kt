@file:OptIn(ExperimentalFoundationApi::class)

package com.github.klee0kai.thekey.app.ui.storages.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.hardResetToPreview
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.ui.storages.presenter.StoragesPresenterDummy
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.LocalScreenResolver
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.navigation.model.StorageItemWidgetState
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.views.DebugDarkContentPreview
import com.github.klee0kai.thekey.core.utils.views.animateTargetCrossFaded
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
    val theme = LocalTheme.current
    val presenter by rememberOnScreenRef { DI.storagesPresenter() }
    val storages by presenter!!.filteredStorages.collectAsState(key = Unit, initial = null)
    val groups by presenter!!.filteredColorGroups.collectAsState(key = Unit, initial = null)
    val showEmpty by animateTargetCrossFaded(storages?.isEmpty(), skipStates = listOf(null))

    if (showEmpty.current == null) {
        // show empty state if need

        // should return here so as not to reset the state of the list
        return
    }

    if (showEmpty.current == true) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .alpha(showEmpty.alpha),
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                text = stringResource(id = R.string.nothig_to_show),
                style = theme.typeScheme.typography.labelSmall,
                color = theme.colorScheme.hintTextColor,
            )
            Spacer(modifier = Modifier.weight(4f))
        }

        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .alpha(showEmpty.alpha),
        state = rememberLazyListState(),
    ) {
        item(key = "header") { header() }

        storages?.forEach { storage ->
            item(contentType = storage::class, key = storage.path) {
                resolver.widget(
                    modifier = Modifier
                        .animateItemPlacement(),
                    widgetState = StorageItemWidgetState(
                        coloredStorage = storage,
                        isPopupMenuAvailable = isPopupMenuAvailable,
                        onClick = { router.backWithResult(storage.path) }
                    )
                )
            }
        }

        item(key = "footer") { footer() }
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
            storagesCount = 8,
        )
    })

    DebugDarkContentPreview {
        StoragesListContent()
    }
}
