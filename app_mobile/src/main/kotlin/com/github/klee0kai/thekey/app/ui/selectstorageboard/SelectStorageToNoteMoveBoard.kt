package com.github.klee0kai.thekey.app.ui.selectstorageboard

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.hardResetToPreview
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.ui.selectstorageboard.presenter.SelectStorageToNoteMoveBoardPresenterDummy
import com.github.klee0kai.thekey.app.ui.simpleboard.components.FavoriteStorageItem
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.views.DebugDarkContentPreview
import com.github.klee0kai.thekey.core.utils.views.collectAsState
import com.github.klee0kai.thekey.core.utils.views.currentRef
import com.github.klee0kai.thekey.core.utils.views.rememberClickDebounced
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef

@Composable
fun SelectStorageToNoteMoveBoard(
    modifier: Modifier = Modifier,
) {
    val router by LocalRouter.currentRef
    val presenter by rememberOnScreenRef { DI.selectStorageToNoteMoveBoardPresenter() }
    val opened by presenter!!.selectableOpenStorages.collectAsState(key = Unit, initial = emptyList())

    LazyColumn(
        modifier = modifier
            .animateContentSize()
    ) {
        if (opened.isNotEmpty()) {
            item("openned_header") {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 30.dp, bottom = 4.dp)
                        .alpha(0.4f),
                    text = stringResource(id = R.string.select_storage_to_move)
                )
            }
        }

        opened.forEach { storage ->
            item(key = "openned-${storage.path}") {
                FavoriteStorageItem(
                    modifier = Modifier,
                    storage = storage,
                    onClick = rememberClickDebounced(storage.path) {
                        presenter?.select(storage.path, router)
                    },
                )
            }
        }
    }
}

@OptIn(DebugOnly::class)
@Preview
@Composable
private fun SelectStorageToNoteMoveBoardPreview() {
    DI.hardResetToPreview()
    DI.initPresenterModule(object : PresentersModule {
        override fun selectStorageToNoteMoveBoardPresenter() =
            SelectStorageToNoteMoveBoardPresenterDummy(
                hasCurrentStorage = true,
                opennedCount = 10,
            )
    })

    DebugDarkContentPreview {
        SelectStorageToNoteMoveBoard()
    }
}
