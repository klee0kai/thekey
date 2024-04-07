@file:OptIn(ExperimentalFoundationApi::class)

package com.github.klee0kai.thekey.app.ui.notegroup.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.domain.model.id
import com.github.klee0kai.thekey.app.ui.storage.notes.ColoredNoteItem
import com.github.klee0kai.thekey.app.utils.views.animateTargetAlphaAsState
import com.github.klee0kai.thekey.app.utils.views.collectAsState


@Preview
@Composable
fun NoteSelectToGroupComponent(
    modifier: Modifier = Modifier,
    storageIdentifier: StorageIdentifier = StorageIdentifier(),
    selectedIds: Set<Long> = emptySet(),
    onSelect: (Long) -> Unit = {},
    header: LazyListScope.() -> Unit = {},
    footer: LazyListScope.() -> Unit = {},
) {
    val presenter = remember { DI.storagePresenter(storageIdentifier) }
    val notes by presenter.filteredNotes.collectAsState(key = Unit, initial = emptyList())

    if (notes.isEmpty()) {
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
    ) {
        header()

        notes.forEach { lazyNote ->
            item(contentType = lazyNote::class, key = lazyNote.id) {

                val icon by animateTargetAlphaAsState(target = if (selectedIds.contains(lazyNote.id)) Icons.Default.Check else Icons.Filled.Add)

                Box(
                    modifier = Modifier
                        .animateItemPlacement(animationSpec = tween())
                        .combinedClickable(onClick = { onSelect.invoke(lazyNote.id) })
                ) {
                    ColoredNoteItem(
                        modifier = Modifier
                            .padding(end = 40.dp),
                        lazyNote = lazyNote,
                    )

                    Icon(
                        modifier = Modifier
                            .alpha(icon.alpha)
                            .align(Alignment.CenterEnd)
                            .padding(end = 16.dp),
                        imageVector = icon.current,
                        contentDescription = "Added"
                    )
                }
            }
        }

        footer()
    }

}