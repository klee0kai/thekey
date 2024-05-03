@file:OptIn(ExperimentalFoundationApi::class)

package com.github.klee0kai.thekey.app.ui.notegroup.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.NoteGroupIdentifier
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.ui.designkit.AppTheme
import com.github.klee0kai.thekey.app.ui.navigation.identifier
import com.github.klee0kai.thekey.app.ui.navigation.model.EditNoteGroupDestination
import com.github.klee0kai.thekey.app.ui.notegroup.presenter.EditNoteGroupsPresenterDummy
import com.github.klee0kai.thekey.app.utils.common.Dummy
import com.github.klee0kai.thekey.app.utils.views.collectAsState
import com.github.klee0kai.thekey.app.utils.views.rememberOnScreenRef


@Composable
fun NoteSelectToGroupComponent(
    modifier: Modifier = Modifier,
    dest: EditNoteGroupDestination = EditNoteGroupDestination(),
    onSelect: (Long, Boolean) -> Unit = { note, selected -> },
    header: @Composable LazyItemScope.() -> Unit = { Spacer(modifier = Modifier.height(12.dp)) },
    footer: @Composable LazyItemScope.() -> Unit = { Spacer(modifier = Modifier.height(12.dp)) },
) {
    val presenter by rememberOnScreenRef { DI.editNoteGroupPresenter(dest.identifier()) }
    val notes by presenter!!.allNotes.collectAsState(key = Unit, initial = emptyList())

    if (notes.isEmpty()) {
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
    ) {
        item { header() }

        notes.forEach { note ->
            item(contentType = note::class, key = note.ptnote) {
                SelectedNoteItem(
                    modifier = Modifier
                        .animateItemPlacement(animationSpec = tween()),
                    note = note,
                    onSelected = { selected ->
                        onSelect.invoke(note.ptnote, selected)
                    }
                )
            }
        }
        item { footer() }
    }
}

@Preview
@Composable
fun NoteSelectToGroupComponentPreview() = AppTheme {
    DI.initPresenterModule(object : PresentersModule {
        override fun editNoteGroupPresenter(id: NoteGroupIdentifier) = EditNoteGroupsPresenterDummy()
    })
    NoteSelectToGroupComponent(dest = EditNoteGroupDestination(groupId = Dummy.dummyId))
}