package com.github.klee0kai.thekey.app.ui.storages.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.hardResetToPreview
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.ui.storages.presenter.StoragesPresenterDummy
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.views.animateAlphaAsState
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreen


@Composable
fun StoragesListContent(
    modifier: Modifier = Modifier,
    showStoragesTitle: Boolean = true,
) {
    val navigator = LocalRouter.current
    val scope = rememberCoroutineScope()
    val presenter = rememberOnScreen { DI.storagesPresenter() }
    val storages = presenter.filteredStorages
        .collectAsState(initial = emptyList(), scope.coroutineContext)
    val titleAnimatedAlpha by animateAlphaAsState(showStoragesTitle)

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
        item {
            Text(
                text = stringResource(id = R.string.storages),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(start = 16.dp, top = 4.dp, bottom = 22.dp)
                    .alpha(titleAnimatedAlpha)
            )
        }

        storages.value.forEach { storage ->
            item(contentType = storage::class) {
                ColoredStorageItem(
                    storage = storage,
                    onClick = { navigator.backWithResult(storage.path) }
                )
            }
        }
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
