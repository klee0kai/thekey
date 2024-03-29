@file:OptIn(ExperimentalFoundationApi::class)

package com.github.klee0kai.thekey.app.ui.storage.notes

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.navigation.LocalRouter
import com.github.klee0kai.thekey.app.ui.navigation.identifier
import com.github.klee0kai.thekey.app.ui.navigation.model.StorageDestination
import com.github.klee0kai.thekey.app.ui.navigation.note
import com.github.klee0kai.thekey.app.utils.views.animateAlphaAsState
import com.github.klee0kai.thekey.app.utils.views.collectAsState

@Preview
@Composable
fun NotesListContent(
    modifier: Modifier = Modifier,
    args: StorageDestination = StorageDestination(),
    showStoragesTitle: Boolean = true,
) {
    val presenter = remember { DI.storagePresenter(args.identifier()) }
    val navigator = LocalRouter.current
    val notes = presenter.notes().collectAsState(key = Unit, initial = listOf())
    val titleAnimatedAlpha by animateAlphaAsState(showStoragesTitle)

    if (notes.value.isEmpty()) {
        return
    }
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
    ) {
        item {
            Text(
                text = stringResource(id = R.string.accounts),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(start = 16.dp, top = 4.dp, bottom = 22.dp)
                    .alpha(titleAnimatedAlpha)
            )
        }

        notes.value.forEach { note ->
            item(contentType = note::class, key = note.value.ptnote) {
                var showMenu by remember { mutableStateOf(false) }

                Box(
                    modifier = Modifier
                        .animateItemPlacement(animationSpec = tween())
                        .combinedClickable(
                            onLongClick = {
                                showMenu = true
                            },
                            onClick = {
                                navigator.navigate(args.note(notePtr = note.value.ptnote))
                            }
                        )
                ) {
                    ColoredNoteItem(lazyNote = note)

                    DropdownMenu(
                        offset = DpOffset(x = (-16).dp, y = 2.dp),
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            modifier = Modifier.align(Alignment.End),
                            text = { Text(text = stringResource(id = R.string.remove)) },
                            onClick = {
                                presenter.remove(note.value.ptnote)
                                showMenu = false
                            }
                        )
                    }
                }
            }
        }
    }
}
