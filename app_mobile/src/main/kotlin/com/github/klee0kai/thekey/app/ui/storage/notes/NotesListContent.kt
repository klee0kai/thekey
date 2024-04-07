@file:OptIn(ExperimentalFoundationApi::class)

package com.github.klee0kai.thekey.app.ui.storage.notes

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.domain.model.LazyColoredNote
import com.github.klee0kai.thekey.app.domain.model.dummyLazyColoredNoteLoaded
import com.github.klee0kai.thekey.app.domain.model.dummyLazyColoredNoteSkeleton
import com.github.klee0kai.thekey.app.domain.model.id
import com.github.klee0kai.thekey.app.ui.designkit.AppTheme
import com.github.klee0kai.thekey.app.ui.designkit.LocalRouter
import com.github.klee0kai.thekey.app.ui.navigation.identifier
import com.github.klee0kai.thekey.app.ui.navigation.model.StorageDestination
import com.github.klee0kai.thekey.app.ui.navigation.note
import com.github.klee0kai.thekey.app.utils.views.animateAlphaAsState
import com.github.klee0kai.thekey.app.utils.views.collectAsState

@Composable
fun NotesListContent(
    modifier: Modifier = Modifier,
    args: StorageDestination = StorageDestination(),
    initList: List<LazyColoredNote> = emptyList(),
    showStoragesTitle: Boolean = true,
) {
    val presenter = remember { DI.storagePresenter(args.identifier()) }
    val router = LocalRouter.current
    val notes by presenter.filteredNotes.collectAsState(key = Unit, initial = initList)
    val groups by presenter.filteredColorGroups.collectAsState(key = Unit, initial = emptyList())
    val titleAnimatedAlpha by animateAlphaAsState(showStoragesTitle)

    if (notes.isEmpty()) return

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
    ) {
        item {
            Text(
                text = stringResource(id = R.string.accounts),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .animateContentSize()
                    .padding(start = 16.dp, top = 4.dp, bottom = 22.dp)
                    .alpha(titleAnimatedAlpha)
            )
        }

        notes.forEach { lazyNote ->
            item(contentType = lazyNote::class, key = lazyNote.id) {
                var showMenu by remember { mutableStateOf(false) }

                Box(
                    modifier = Modifier
                        .animateItemPlacement(animationSpec = tween())
                        .combinedClickable(
                            onLongClick = { showMenu = true },
                            onClick = {
                                router.navigate(args.note(notePtr = lazyNote.id))
                            }
                        )
                ) {
                    ColoredNoteItem(lazyNote = lazyNote)

                    DropdownMenu(
                        offset = DpOffset(x = (-16).dp, y = 2.dp),
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        NoteDropDownMenuContent(
                            colorGroups = groups.map { it.placeholder },
                            selectedGroupId = lazyNote.getOrNull()?.group?.id,
                            onColorGroupSelected = {
                                presenter.setColorGroup(notePt = lazyNote.id, groupId = it.id)
                                showMenu = false
                            },
                            onEdit = {
                                router.navigate(args.note(lazyNote.id))
                                showMenu = false
                            }
                        )
                    }
                }
            }
        }
    }
}


@Preview
@Composable
private fun NotesListContentPreview() {
    AppTheme {
        NotesListContent(
            initList = listOf(
                dummyLazyColoredNoteSkeleton(),
                dummyLazyColoredNoteSkeleton(),
                dummyLazyColoredNoteLoaded(),
                dummyLazyColoredNoteLoaded(),
                dummyLazyColoredNoteLoaded(),
            ),
            showStoragesTitle = false,
        )
    }
}