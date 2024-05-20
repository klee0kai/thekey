@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.github.klee0kai.thekey.app.ui.storage.notes

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.hardResetToPreview
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.ui.designkit.components.FabSimpleInContainer
import com.github.klee0kai.thekey.app.ui.designkit.components.appbar.AppBarConst
import com.github.klee0kai.thekey.app.ui.designkit.components.bottomsheet.SimpleBottomSheetScaffold
import com.github.klee0kai.thekey.app.ui.designkit.components.bottomsheet.topContentAlphaFromDrag
import com.github.klee0kai.thekey.app.ui.designkit.components.bottomsheet.topContentOffsetFromDrag
import com.github.klee0kai.thekey.app.ui.navigation.createGroup
import com.github.klee0kai.thekey.app.ui.navigation.editGroup
import com.github.klee0kai.thekey.app.ui.navigation.identifier
import com.github.klee0kai.thekey.app.ui.navigation.model.StorageDestination
import com.github.klee0kai.thekey.app.ui.navigation.note
import com.github.klee0kai.thekey.app.ui.storage.StorageScreen
import com.github.klee0kai.thekey.app.ui.storage.presenter.StoragePresenterDummy
import com.github.klee0kai.thekey.app.ui.storages.components.GroupsSelectContent
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.common.Dummy
import com.github.klee0kai.thekey.core.utils.views.animateAlphaAsState
import com.github.klee0kai.thekey.core.utils.views.collectAsState
import com.github.klee0kai.thekey.core.utils.views.rememberDerivedStateOf
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef
import com.github.klee0kai.thekey.core.utils.views.topDp
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import org.jetbrains.annotations.VisibleForTesting

@Composable
fun NotesContent(
    modifier: Modifier = Modifier,
    dest: StorageDestination = StorageDestination(),
    secondaryTabsHeight: Dp = 0.dp,
    isPageFullyAvailable: Boolean = false,
    onDrag: (Float) -> Unit = {},
) {
    val presenter by rememberOnScreenRef { DI.storagePresenter(dest.identifier()) }
    val router = LocalRouter.current
    val selectedGroup by presenter!!.selectedGroupId.collectAsState(key = Unit, initial = null)
    val groups by presenter!!.filteredColorGroups.collectAsState(key = Unit, initial = emptyList())
    var dragProgress by remember { mutableFloatStateOf(0f) }
    val addButtonAlpha by animateAlphaAsState(isPageFullyAvailable)
    val addButtonVisible by rememberDerivedStateOf { addButtonAlpha > 0 }
    val showStoragesTitle by rememberDerivedStateOf { dragProgress > 0.1f }

    SimpleBottomSheetScaffold(
        modifier = modifier,
        topContentSize = 170.dp + AppBarConst.appBarSize,
        topContentModifier = Modifier.padding(top = AppBarConst.appBarSize),
        topMargin = secondaryTabsHeight + WindowInsets.safeContent.topDp,
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
                onAdd = { router.navigate(dest.createGroup()) },
                colorGroups = groups,
                onGroupSelected = { presenter?.selectGroup(it.id) },
                onGroupEdit = { router.navigate(dest.editGroup(it.id)) },
                onGroupDelete = { presenter?.deleteGroup(it.id) }
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
                .windowInsetsPadding(WindowInsets.safeContent)
                .alpha(addButtonAlpha),
            onClick = { router.navigate(dest.note()) },
            content = { Icon(Icons.Default.Add, contentDescription = "Add") }
        )
    }
}

@OptIn(DebugOnly::class)
@VisibleForTesting
@Preview(device = Devices.PHONE)
@Composable
fun NotesContentPreview() = EdgeToEdgeTemplate {
    AppTheme {
        DI.hardResetToPreview()
        DI.initPresenterModule(object : PresentersModule {
            override fun storagePresenter(storageIdentifier: StorageIdentifier) = StoragePresenterDummy()
        })
        StorageScreen(
            dest = StorageDestination(path = Dummy.unicString, version = 2)
        )
    }
}