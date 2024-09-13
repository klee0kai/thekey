@file:OptIn(ExperimentalFoundationApi::class)

package com.github.klee0kai.thekey.app.ui.notegroup.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.hardResetToPreview
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.ui.navigation.identifier
import com.github.klee0kai.thekey.app.ui.navigation.model.EditNoteGroupDestination
import com.github.klee0kai.thekey.app.ui.notegroup.presenter.EditNoteGroupsPresenterDummy
import com.github.klee0kai.thekey.app.ui.storage.notes.ColoredNoteItem
import com.github.klee0kai.thekey.app.ui.storage.notes.ColoredOtpNoteItem
import com.github.klee0kai.thekey.core.di.identifiers.NoteGroupIdentifier
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.icons.AddCheckedIcon
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.common.Dummy
import com.github.klee0kai.thekey.core.utils.views.DebugDarkContentPreview
import com.github.klee0kai.thekey.core.utils.views.animateTargetCrossFaded
import com.github.klee0kai.thekey.core.utils.views.collectAsState
import com.github.klee0kai.thekey.core.utils.views.currentRef
import com.github.klee0kai.thekey.core.utils.views.ifProduction
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef
import org.jetbrains.annotations.VisibleForTesting


@Composable
fun NoteSelectToGroupComponent(
    modifier: Modifier = Modifier,
    dest: EditNoteGroupDestination = EditNoteGroupDestination(),
    isOtpMode: Boolean = false,
    onSelect: (String, Boolean) -> Unit = { id, selected -> },
    header: @Composable LazyItemScope.() -> Unit = { Spacer(modifier = Modifier.height(12.dp)) },
    footer: @Composable LazyItemScope.() -> Unit = { Spacer(modifier = Modifier.height(12.dp)) },
) {
    val router by LocalRouter.currentRef
    val theme = LocalTheme.current
    val safeContentPadding = WindowInsets.safeContent.asPaddingValues()
    val presenter by rememberOnScreenRef { DI.editNoteGroupPresenter(dest.identifier()) }
    val storageItems by presenter!!.filteredItems.collectAsState(key = Unit, initial = emptyList())
    val isOtpModeAnimated by animateTargetCrossFaded(target = isOtpMode)

    if (storageItems.isEmpty()) {
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
    ) {
        item("header") { header() }

        storageItems.forEach { storageItem ->
            val note = storageItem.note
            val otp = storageItem.otp

            when {
                note != null -> {
                    item(key = storageItem.id, contentType = note::class) {
                        ColoredNoteItem(
                            modifier = Modifier
                                .ifProduction { animateItemPlacement() }
                                .combinedClickable(
                                    onClick = {
                                        onSelect.invoke(storageItem.id, !storageItem.selected)
                                    },
                                ),
                            note = note,
                            icon = {
                                if (!isOtpModeAnimated.current) {
                                    AddCheckedIcon(
                                        modifier = Modifier
                                            .alpha(isOtpModeAnimated.alpha),
                                        isAdded = storageItem.selected,
                                    )
                                }
                            }
                        )
                    }
                }

                otp != null -> {
                    item(key = storageItem.id, contentType = otp::class) {
                        ColoredOtpNoteItem(
                            modifier = Modifier
                                .ifProduction { animateItemPlacement() }
                                .combinedClickable(
                                    onClick = {
                                        onSelect.invoke(storageItem.id, !storageItem.selected)
                                    },
                                ),
                            otp = otp,
                            icon = {
                                if (!isOtpModeAnimated.current) {
                                    AddCheckedIcon(
                                        modifier = Modifier
                                            .alpha(isOtpModeAnimated.alpha),
                                        isAdded = storageItem.selected,
                                    )
                                }
                            }
                        )
                    }
                }

            }
        }
        item("footer") { footer() }
    }
}

@OptIn(DebugOnly::class)
@VisibleForTesting
@Preview
@Composable
fun NoteSelectToGroupComponentPreview() {
    DI.hardResetToPreview()
    DI.initPresenterModule(object : PresentersModule {
        override fun editNoteGroupPresenter(id: NoteGroupIdentifier) =
            EditNoteGroupsPresenterDummy()
    })
    DebugDarkContentPreview {
        NoteSelectToGroupComponent(
            dest = EditNoteGroupDestination(
                groupId = Dummy.dummyId,
            ),
        )
    }
}