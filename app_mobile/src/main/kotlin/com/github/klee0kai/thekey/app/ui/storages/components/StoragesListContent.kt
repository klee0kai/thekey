package com.github.klee0kai.thekey.app.ui.storages.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.navigation.LocalRouter
import com.github.klee0kai.thekey.app.utils.views.animateAlphaAsState


@Preview
@Composable
fun StoragesListContent(
    modifier: Modifier = Modifier,
    showStoragesTitle: Boolean = true,
) {
    val navigator = LocalRouter.current
    val scope = rememberCoroutineScope()
    val presenter = remember { DI.storagesPresenter() }
    val storages = presenter.storages()
        .collectAsState(initial = emptyList(), scope.coroutineContext)

    val titleAnimatedAlpha by animateAlphaAsState(showStoragesTitle)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
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

