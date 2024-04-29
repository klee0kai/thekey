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
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.NoteGroupIdentifier
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.ui.designkit.AppTheme
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
import com.github.klee0kai.thekey.app.ui.notegroup.model.EditNoteGroupsState
import com.github.klee0kai.thekey.app.ui.notegroup.presenter.EditNoteGroupsPresenterDummy
import com.github.klee0kai.thekey.app.ui.notegroup.presenter.selectNote
import com.github.klee0kai.thekey.app.utils.common.Dummy
import com.github.klee0kai.thekey.app.utils.views.collectAsState
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun EditNoteGroupsScreen(
    dest: EditNoteGroupDestination = EditNoteGroupDestination(),
) {
    val presenter = remember { DI.editNoteGroupPresenter(dest.identifier()).apply { init() } }
    val router = LocalRouter.current
    val groupNameFieldFocusRequester = remember { FocusRequester() }
    val state by presenter.state.collectAsState(key = Unit, initial = EditNoteGroupsState())

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
                    .height(scaffoldState.topContentSize)
                    .alpha(dragProgress.topContentAlphaFromDrag())
                    .offset(y = dragProgress.topContentOffsetFromDrag()),
                groupNameFieldModifier = Modifier
                    .focusRequester(groupNameFieldFocusRequester),
                select = state.color,
                groupName = state.name,
                onChangeGroupName = { presenter.input { copy(name = it.take(1)) } },
                onSelect = {
                    groupNameFieldFocusRequester.freeFocus()
                    presenter.input { copy(color = it) }
                }
            )
        },
        sheetContent = {
            NoteSelectToGroupComponent(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxSize(),
                dest = dest,
                onSelect = { notePt, selected -> presenter.selectNote(notePt, selected) },
                footer = { Spacer(modifier = Modifier.height(200.dp)) }
            )
        }
    )

    AppBarStates(
        modifier = Modifier,
        navigationIcon = {
            IconButton(onClick = remember { { router.back() } }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = null)
            }
        },
        titleContent = { Text(text = stringResource(id = if (state.isEditMode) R.string.edit_group else R.string.create_group)) },
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


@Preview(
    device = Devices.PIXEL_6,
    showSystemUi = true,
)
@Composable
private fun EditNoteGroupsSkeletonPreview() = AppTheme {
    DI.initPresenterModule(object : PresentersModule {
        override fun editNoteGroupPresenter(id: NoteGroupIdentifier) = object : EditNoteGroupsPresenterDummy() {
            override val state = MutableStateFlow(
                EditNoteGroupsState(
                    isSkeleton = true,
                    isEditMode = false,
                )
            )
        }
    })
    EditNoteGroupsScreen(dest = EditNoteGroupDestination(groupId = Dummy.dummyId))
}
