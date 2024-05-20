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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.hardResetToPreview
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.ui.designkit.AppTheme
import com.github.klee0kai.thekey.app.ui.designkit.LocalRouter
import com.github.klee0kai.thekey.app.ui.navigation.identifier
import com.github.klee0kai.thekey.app.ui.navigation.model.StorageDestination
import com.github.klee0kai.thekey.app.ui.navigation.note
import com.github.klee0kai.thekey.app.ui.navigation.otpNote
import com.github.klee0kai.thekey.app.ui.storage.presenter.StoragePresenterDummy
import com.github.klee0kai.thekey.app.ui.storage.presenter.StoragePresenterLongListDummy
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.views.animateAlphaAsState
import com.github.klee0kai.thekey.core.utils.views.animateContentSizeProduction
import com.github.klee0kai.thekey.core.utils.views.bottomDp
import com.github.klee0kai.thekey.core.utils.views.collectAsState
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
    val presenter by rememberOnScreenRef { DI.storagePresenter(args.identifier()) }
    val router = LocalRouter.current
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
        item {
            Text(
                text = stringResource(id = R.string.accounts),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = 16.dp, top = 4.dp, bottom = 22.dp)
                    .animateContentSizeProduction()
                    .alpha(titleAnimatedAlpha)
            )
        }

        storageItems?.forEach { storageItem ->
            val note = storageItem.note
            val otp = storageItem.otp

            if (note != null) {
                item(
                    contentType = note::class,
                    key = note.ptnote
                ) {
                    var showMenu by remember { mutableStateOf(false) }

                    ColoredNoteItem(
                        modifier = Modifier
                            .animateContentSizeProduction()
                            .combinedClickable(
                                onLongClick = {
                                    showMenu = true
                                },
                                onClick = {
                                    router.navigate(args.note(notePtr = note.ptnote))
                                }
                            ),
                        note = note,
                        overlayContent = {
                            DropdownMenu(
                                offset = DpOffset(x = (-16).dp, y = 2.dp),
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {

                                NoteDropDownMenuContent(
                                    colorGroups = groups,
                                    selectedGroupId = note.group.id,
                                    onColorGroupSelected = {
                                        presenter?.setColorGroup(notePt = note.ptnote, groupId = it.id)
                                        showMenu = false
                                    },
                                    onEdit = {
                                        router.navigate(args.note(note.ptnote))
                                        showMenu = false
                                    }
                                )
                            }
                        }
                    )
                }
            }

            if (otp != null) {
                item(
                    contentType = otp::class,
                    key = otp.ptnote
                ) {
                    ColoredOtpNoteItem(
                        modifier = Modifier
                            .animateContentSizeProduction()
                            .combinedClickable(
                                onLongClick = {
                                },
                                onClick = {
                                    router.navigate(args.otpNote(notePtr = otp.ptnote))
                                }
                            ),
                        otp = otp,
                        overlayContent = {

                        }
                    )
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
    AppTheme {
        DI.hardResetToPreview()
        DI.initPresenterModule(object : PresentersModule {
            override fun storagePresenter(storageIdentifier: StorageIdentifier) = StoragePresenterDummy()
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
    AppTheme {
        DI.hardResetToPreview()
        DI.initPresenterModule(object : PresentersModule {
            override fun storagePresenter(storageIdentifier: StorageIdentifier) = StoragePresenterLongListDummy(notesCount = 20)
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
    AppTheme {
        DI.hardResetToPreview()
        DI.initPresenterModule(object : PresentersModule {
            override fun storagePresenter(storageIdentifier: StorageIdentifier) = StoragePresenterDummy()
        })
        NotesListContent(
            showStoragesTitle = true,
        )
    }
}