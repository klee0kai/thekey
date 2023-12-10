package com.github.klee0kai.thekey.app.ui.storages.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.model.ColoredStorage
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf


@Preview
@Composable
fun StoragesListContent(
    modifier: Modifier = Modifier,
    showStoragesTitle: Boolean = true,
) {

    val storages = storagesState()
    val titleAnimatedAlpha by animateFloatAsState(
        targetValue = if (showStoragesTitle) 1.0f else 0f,
        label = "title animate"
    )

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
            item(
                key = storage.path,
                contentType = storage::class,
            ) {
                ColoredStorageItem(storage)
            }
        }
    }
}

@Composable
private fun storagesState(): State<List<ColoredStorage>> {
    val scope = rememberCoroutineScope()
    val presenter = remember { DI.storagesPresenter() }
    return if (LocalView.current.isInEditMode) {
        flowOf((0..100).map {
            ColoredStorage("/path${it}", "name${it}", "description${it}")
        })
    } else {
        flow { presenter.storages().collect { emit(it) } }
    }.collectAsState(initial = emptyList(), scope.coroutineContext)
}
