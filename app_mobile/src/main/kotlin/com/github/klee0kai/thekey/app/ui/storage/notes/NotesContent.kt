@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.github.klee0kai.thekey.app.ui.storage.notes

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.hardResetToPreview
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.ui.navigation.editGroup
import com.github.klee0kai.thekey.app.ui.navigation.editNote
import com.github.klee0kai.thekey.app.ui.navigation.identifier
import com.github.klee0kai.thekey.app.ui.navigation.model.StorageDestination
import com.github.klee0kai.thekey.app.ui.storage.StorageScreen
import com.github.klee0kai.thekey.app.ui.storage.presenter.StoragePresenterDummy
import com.github.klee0kai.thekey.app.ui.storages.components.GroupsSelectContent
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.domain.model.otpNotes
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.bottomsheet.SimpleBottomSheetScaffold
import com.github.klee0kai.thekey.core.ui.devkit.bottomsheet.topContentAlphaFromDrag
import com.github.klee0kai.thekey.core.ui.devkit.bottomsheet.topContentOffsetFromDrag
import com.github.klee0kai.thekey.core.ui.devkit.components.FabSimpleInContainer
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarConst
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.common.Dummy
import com.github.klee0kai.thekey.core.utils.views.DebugDarkScreenPreview
import com.github.klee0kai.thekey.core.utils.views.animateAlphaAsState
import com.github.klee0kai.thekey.core.utils.views.collectAsState
import com.github.klee0kai.thekey.core.utils.views.currentRef
import com.github.klee0kai.thekey.core.utils.views.rememberClickDebounced
import com.github.klee0kai.thekey.core.utils.views.rememberClickDebouncedArg
import com.github.klee0kai.thekey.core.utils.views.rememberDerivedStateOf
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef
import com.github.klee0kai.thekey.core.utils.views.rememberTargetFaded
import org.jetbrains.annotations.VisibleForTesting
import com.github.klee0kai.thekey.core.R as CoreR

@Composable
fun NotesContent(
    modifier: Modifier = Modifier,
    dest: StorageDestination = StorageDestination(),
    secondaryTabsHeight: Dp = 0.dp,
    isPageFullyAvailable: Boolean = false,
    onDrag: (Float) -> Unit = {},
) {
    val theme = LocalTheme.current
    val router by LocalRouter.currentRef
    val safeContentPaddings = WindowInsets.safeContent.asPaddingValues()
    val presenter by rememberOnScreenRef { DI.storagePresenter(dest.identifier()) }
    val selectedGroup by presenter!!.selectedGroupId.collectAsState(key = Unit, initial = null)
    val groups by presenter!!.filteredColorGroups.collectAsState(key = Unit, initial = emptyList())
    val otpGroupSelected by rememberTargetFaded {
        if (selectedGroup == ColorGroup.otpNotes().id) {
            groups.firstOrNull { it.id == selectedGroup }
        } else {
            null
        }
    }

    var dragProgress by remember { mutableFloatStateOf(0f) }
    val addButtonAlpha by animateAlphaAsState(isPageFullyAvailable)
    val addButtonVisible by rememberDerivedStateOf { addButtonAlpha > 0 }
    val showStoragesTitle by rememberDerivedStateOf { dragProgress > 0.1f }

    SimpleBottomSheetScaffold(
        modifier = modifier,
        topContentSize = 170.dp + AppBarConst.appBarSize,
        topContentModifier = Modifier.padding(top = AppBarConst.appBarSize),
        topMargin = secondaryTabsHeight + safeContentPaddings.calculateTopPadding(),
        onDrag = {
            dragProgress = it
            onDrag.invoke(it)
        },
        topContent = {
            GroupsSelectContent(
                modifier = Modifier
                    .alpha(dragProgress.topContentAlphaFromDrag())
                    .offset(y = dragProgress.topContentOffsetFromDrag()),
                selectedGroup = selectedGroup,
                onAdd = rememberClickDebounced { presenter?.addNewNoteGroup(router) },
                colorGroups = groups,
                onGroupSelected = rememberClickDebouncedArg { presenter?.selectGroup(it.id) },
                onGroupEdit = rememberClickDebouncedArg { router?.navigate(dest.editGroup(it.id)) },
            )
        },
        sheetContent = {
            NotesListContent(
                modifier = Modifier.fillMaxSize(),
                args = dest,
                showStoragesTitle = showStoragesTitle,
            )
        },
    )

    if (addButtonVisible) {
        FabSimpleInContainer(
            modifier = Modifier
                .alpha(addButtonAlpha),
            square = otpGroupSelected.current != null,
            containerColor = if (otpGroupSelected.current != null) {
                theme.colorScheme.surfaceSchemas.surfaceScheme(otpGroupSelected.current!!.keyColor).surfaceColor
            } else {
                theme.colorScheme.androidColorScheme.secondaryContainer
            },
            onClick = rememberClickDebounced {
                if (otpGroupSelected.current != null) {
                    presenter?.scanNewOtpQRCode(router)
                } else {
                    router?.navigate(dest.editNote())
                }
            },
            content = {
                if (otpGroupSelected.current != null) {
                    Icon(
                        painter = painterResource(id = CoreR.drawable.ic_qrcode_scanner),
                        modifier = Modifier.alpha(otpGroupSelected.alpha),
                        contentDescription = "ScanQR",
                    )
                } else {
                    Icon(
                        Icons.Default.Add,
                        modifier = Modifier.alpha(otpGroupSelected.alpha),
                        contentDescription = "Add"
                    )
                }
            }
        )
    }
}

@OptIn(DebugOnly::class)
@VisibleForTesting
@Preview(device = Devices.PHONE)
@Composable
fun NotesContentPreview() {
    DI.hardResetToPreview()
    DI.initPresenterModule(object : PresentersModule {
        override fun storagePresenter(storageIdentifier: StorageIdentifier) =
            StoragePresenterDummy()
    })

    DebugDarkScreenPreview {
        StorageScreen(
            dest = StorageDestination(
                path = Dummy.unicString, version = 2,
                selectedPage = 0
            ),
        )
    }
}