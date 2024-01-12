package com.github.klee0kai.thekey.app.ui.storage.notes

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.ui.navigation.NoteDestination
import com.github.klee0kai.thekey.app.utils.common.animateAlphaAsState
import com.github.klee0kai.thekey.app.utils.coroutine.awaitAsState
import dev.olshevski.navigation.reimagined.navigate

@Preview
@Composable
fun NotesListContent(
    modifier: Modifier = Modifier,
    storagePath: String = "",
    showStoragesTitle: Boolean = true,
) {
    val presenter = remember { DI.storagePresenter(StorageIdentifier(storagePath)) }
    val navigator = remember { DI.navigator() }
    val notes = presenter.notes().awaitAsState(initial = emptyList())

    val titleAnimatedAlpha by animateAlphaAsState(showStoragesTitle)

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
            item {
                ColoredNoteItem(
                    note = note,
                    onClick = {
                        navigator.navigate(NoteDestination(path = storagePath, notePtr = note.ptnote))
                    }
                )
            }
        }
    }
}
