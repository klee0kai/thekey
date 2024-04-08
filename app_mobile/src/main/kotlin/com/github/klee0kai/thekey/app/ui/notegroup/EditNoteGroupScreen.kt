package com.github.klee0kai.thekey.app.ui.notegroup

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.designkit.LocalRouter
import com.github.klee0kai.thekey.app.ui.designkit.components.appbar.AppBarConst
import com.github.klee0kai.thekey.app.ui.designkit.components.appbar.AppBarStates
import com.github.klee0kai.thekey.app.ui.designkit.components.bottomsheet.SimpleBottomSheetScaffold
import com.github.klee0kai.thekey.app.ui.designkit.components.bottomsheet.rememberSimpleBottomSheetScaffoldState
import com.github.klee0kai.thekey.app.ui.designkit.components.bottomsheet.topContentAlphaFromDrag
import com.github.klee0kai.thekey.app.ui.designkit.components.bottomsheet.topContentOffsetFromDrag
import com.github.klee0kai.thekey.app.ui.navigation.identifier
import com.github.klee0kai.thekey.app.ui.navigation.model.EditNoteGroupDestination
import com.github.klee0kai.thekey.app.ui.notegroup.components.EditGroupInfoContent
import com.github.klee0kai.thekey.app.ui.notegroup.components.NoteSelectToGroupComponent

@Preview
@Composable
fun EditNoteGroupScreen(
    dest: EditNoteGroupDestination = EditNoteGroupDestination(),
) {
    val presenter = remember { DI.editNoteGroupPresenter(dest.identifier()) }
    val router = LocalRouter.current
    val groupNameFieldFocusRequester = remember { FocusRequester() }
    val isCreate = dest.groupId == null
    val selectedColor by presenter.selectedKeyColor.collectAsState()
    val selectedNotes by presenter.selectedNotes.collectAsState()
    val name by presenter.name.collectAsState()

    var dragProgress by remember { mutableFloatStateOf(0f) }
    val scaffoldState = rememberSimpleBottomSheetScaffoldState(
        topContentSize = 190.dp,
        appBarSize = AppBarConst.appBarSize
    )

    SimpleBottomSheetScaffold(
        simpleBottomSheetScaffoldState = scaffoldState,
        onDrag = { dragProgress = it },
        topContent = {
            EditGroupInfoContent(
                modifier = Modifier
                    .alpha(dragProgress.topContentAlphaFromDrag())
                    .offset(y = dragProgress.topContentOffsetFromDrag()),
                groupNameFieldModifier = Modifier
                    .focusRequester(groupNameFieldFocusRequester),
                select = selectedColor,
                groupName = name,
                onChangeGroupName = { presenter.name.value = it.take(1) },
                onSelect = {
                    groupNameFieldFocusRequester.freeFocus()
                    presenter.selectedKeyColor.value = it
                }
            )
        },
        sheetContent = {
            NoteSelectToGroupComponent(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxSize(),
                storageIdentifier = dest.storageIdentifier,
                selectedIds = selectedNotes,
                onSelect = { presenter.noteSelected(it) },
                footer = {
                    item { Spacer(modifier = Modifier.height(200.dp)) }
                }
            )
        }
    )

    AppBarStates(
        navigationIcon = {
            IconButton(onClick = remember { { router.back() } }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = null)
            }
        },
        titleContent = { Text(text = stringResource(id = if (isCreate) R.string.create_group else R.string.edit_group)) },
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = 16.dp + AppBarConst.appBarSize,
                bottom = 16.dp,
                start = 16.dp,
                end = 16.dp
            ),
    ) {
        FilledTonalButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            onClick = { presenter.save() }
        ) {
            Text(stringResource(R.string.save))
        }
    }
}

