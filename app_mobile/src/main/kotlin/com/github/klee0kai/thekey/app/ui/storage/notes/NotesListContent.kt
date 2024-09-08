@file:OptIn(ExperimentalFoundationApi::class)

package com.github.klee0kai.thekey.app.ui.storage.notes

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.hardResetToPreview
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.ui.navigation.editNote
import com.github.klee0kai.thekey.app.ui.navigation.editOtpNote
import com.github.klee0kai.thekey.app.ui.navigation.identifier
import com.github.klee0kai.thekey.app.ui.navigation.model.StorageDestination
import com.github.klee0kai.thekey.app.ui.navigation.note
import com.github.klee0kai.thekey.app.ui.navigation.otpNote
import com.github.klee0kai.thekey.app.ui.storage.notes.popup.NotePopupMenu
import com.github.klee0kai.thekey.app.ui.storage.presenter.StoragePresenterDummy
import com.github.klee0kai.thekey.app.ui.storage.presenter.StoragePresenterLongListDummy
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.overlay.PopupMenu
import com.github.klee0kai.thekey.core.ui.devkit.theme.DefaultThemes
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.possitions.onGlobalPositionState
import com.github.klee0kai.thekey.core.utils.possitions.rememberViewPosition
import com.github.klee0kai.thekey.core.utils.views.animateAlphaAsState
import com.github.klee0kai.thekey.core.utils.views.animateContentSizeProduction
import com.github.klee0kai.thekey.core.utils.views.bottomDp
import com.github.klee0kai.thekey.core.utils.views.collectAsState
import com.github.klee0kai.thekey.core.utils.views.ifProduction
import com.github.klee0kai.thekey.core.utils.views.rememberClickDebounced
import com.github.klee0kai.thekey.core.utils.views.rememberClickDebouncedArg
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef
import de.drick.compose.edgetoedgepreviewlib.CameraCutoutMode
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import org.jetbrains.annotations.VisibleForTesting

@Composable
fun NotesListContent(
    modifier: Modifier = Modifier,
    args: StorageDestination = StorageDestination(),
    showStoragesTitle: Boolean = true,
) {
    val router = LocalRouter.current
    val theme = LocalTheme.current
    val presenter by rememberOnScreenRef { DI.storagePresenter(args.identifier()) }
    val storageItems by presenter!!.filteredItems.collectAsState(key = Unit, initial = null)
    val groups by presenter!!.filteredColorGroups.collectAsState(key = Unit, initial = emptyList())
    val titleAnimatedAlpha by animateAlphaAsState(showStoragesTitle)

    if (storageItems == null) return

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .animateContentSizeProduction(),
        contentPadding = PaddingValues(bottom = WindowInsets.safeContent.bottomDp)
    ) {
        item("header") {
            Text(
                text = stringResource(id = R.string.accounts),
                style = theme.typeScheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = 16.dp, top = 4.dp, bottom = 22.dp)
                    .ifProduction { animateItemPlacement() }
                    .alpha(titleAnimatedAlpha)
            )
        }

        storageItems?.forEach { storageItem ->
            val note = storageItem.note
            val otp = storageItem.otp

            if (note != null) {
                item(
                    key = "note-${note.id}",
                    contentType = note::class,
                ) {
                    var showMenu by remember { mutableStateOf(false) }
                    val position = rememberViewPosition()

                    ColoredNoteItem(
                        modifier = Modifier
                            .ifProduction { animateItemPlacement() }
                            .onGlobalPositionState(position)
                            .combinedClickable(
                                onLongClick = rememberClickDebounced(note) {
                                    showMenu = true
                                },
                                onClick = rememberClickDebounced(note) {
                                    router.navigate(args.note(notePtr = note.id))
                                }
                            ),
                        note = note,
                    )
                    PopupMenu(
                        visible = showMenu,
                        positionAnchor = position,
                        horizontalBias = 0.8f,
                        onDismissRequest = { showMenu = false }
                    ) {
                        NotePopupMenu(
                            modifier = Modifier.padding(vertical = 4.dp),
                            colorGroups = groups,
                            selectedGroupId = note.group.id,
                            onColorGroupSelected = rememberClickDebouncedArg(note) {
                                showMenu = false
                                presenter?.setColorGroup(notePt = note.id, groupId = it.id)
                            },
                            onEdit = rememberClickDebounced(note) {
                                showMenu = false
                                router.navigate(args.editNote(note.id))
                            }
                        )

                    }
                }
            }

            if (otp != null) {
                item(
                    key = "otp-${otp.id}",
                    contentType = otp::class,
                ) {
                    var showMenu by remember { mutableStateOf(false) }
                    val position = rememberViewPosition()

                    ColoredOtpNoteItem(
                        modifier = Modifier
                            .ifProduction { animateItemPlacement() }
                            .onGlobalPositionState(position)
                            .combinedClickable(
                                onLongClick = rememberClickDebounced(note) {
                                    showMenu = true
                                },
                                onClick = rememberClickDebounced(otp) {
                                    router.navigate(args.otpNote(otpNotePtr = otp.id))
                                }
                            ),
                        otp = otp,
                    )

                    PopupMenu(
                        visible = showMenu,
                        positionAnchor = position,
                        horizontalBias = 0.8f,
                        onDismissRequest = { showMenu = false }
                    ) {
                        NotePopupMenu(
                            modifier = Modifier.padding(vertical = 4.dp),
                            colorGroups = groups,
                            selectedGroupId = otp.group.id,
                            onColorGroupSelected = rememberClickDebouncedArg(note) {
                                showMenu = false
                                presenter?.setOtpColorGroup(
                                    otpNotePtr = otp.id,
                                    groupId = it.id,
                                )
                            },
                            onEdit = rememberClickDebounced(note) {
                                showMenu = false
                                router.navigate(args.editOtpNote(otpNotePtr = otp.id))
                            }
                        )

                    }

                }
            }
        }
    }
}

@OptIn(DebugOnly::class)
@VisibleForTesting
@Preview
@Composable
fun NotesListContentPreview() = EdgeToEdgeTemplate(
    isStatusBarVisible = false,
    cameraCutoutMode = CameraCutoutMode.None,
) {
    AppTheme(theme = DefaultThemes.darkTheme) {
        DI.hardResetToPreview()
        DI.initPresenterModule(object : PresentersModule {
            override fun storagePresenter(storageIdentifier: StorageIdentifier) =
                StoragePresenterDummy()
        })
        NotesListContent(
            showStoragesTitle = false,
        )
    }
}

@OptIn(DebugOnly::class)
@VisibleForTesting
@Preview
@Composable
fun NotesLongListContentPreview() = EdgeToEdgeTemplate(
    isStatusBarVisible = false,
    cameraCutoutMode = CameraCutoutMode.None,
) {
    AppTheme(theme = DefaultThemes.darkTheme) {
        DI.hardResetToPreview()
        DI.initPresenterModule(object : PresentersModule {
            override fun storagePresenter(storageIdentifier: StorageIdentifier) =
                StoragePresenterLongListDummy(notesCount = 20)
        })
        NotesListContent(
            showStoragesTitle = false,
        )
    }
}

@OptIn(DebugOnly::class)
@VisibleForTesting
@Preview
@Composable
fun NotesListContentTitlePreview() = EdgeToEdgeTemplate(
    isStatusBarVisible = false,
    cameraCutoutMode = CameraCutoutMode.None,
) {
    AppTheme(theme = DefaultThemes.darkTheme) {
        DI.hardResetToPreview()
        DI.initPresenterModule(object : PresentersModule {
            override fun storagePresenter(storageIdentifier: StorageIdentifier) =
                StoragePresenterDummy()
        })
        NotesListContent(
            showStoragesTitle = true,
        )
    }
}