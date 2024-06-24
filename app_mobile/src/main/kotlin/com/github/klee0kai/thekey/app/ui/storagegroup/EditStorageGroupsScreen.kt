@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.klee0kai.thekey.app.ui.storagegroup

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.hardResetToPreview
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.ui.navigation.identifier
import com.github.klee0kai.thekey.app.ui.navigation.model.EditStorageGroupDestination
import com.github.klee0kai.thekey.app.ui.notegroup.components.EditGroupInfoContent
import com.github.klee0kai.thekey.app.ui.storagegroup.components.StorageSelectToGroupComponent
import com.github.klee0kai.thekey.app.ui.storagegroup.model.EditStorageGroupsState
import com.github.klee0kai.thekey.app.ui.storagegroup.presenter.EditStoragesGroupPresenterDummy
import com.github.klee0kai.thekey.app.ui.storagegroup.presenter.selectStorage
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.di.identifiers.StorageGroupIdentifier
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.bottomsheet.SimpleBottomSheetScaffold
import com.github.klee0kai.thekey.core.ui.devkit.bottomsheet.topContentAlphaFromDrag
import com.github.klee0kai.thekey.core.ui.devkit.bottomsheet.topContentOffsetFromDrag
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarConst
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarStates
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.DeleteIconButton
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.DoneIconButton
import com.github.klee0kai.thekey.core.ui.devkit.theme.DefaultThemes
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.common.Dummy
import com.github.klee0kai.thekey.core.utils.views.animateTargetCrossFaded
import com.github.klee0kai.thekey.core.utils.views.collectAsState
import com.github.klee0kai.thekey.core.utils.views.currentRef
import com.github.klee0kai.thekey.core.utils.views.horizontal
import com.github.klee0kai.thekey.core.utils.views.isIme
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef
import com.github.klee0kai.thekey.core.utils.views.rememberTargetCrossFaded
import com.github.klee0kai.thekey.core.utils.views.topDp
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import kotlinx.coroutines.flow.MutableStateFlow
import org.jetbrains.annotations.VisibleForTesting

@Composable
fun EditStorageGroupsScreen(
    dest: EditStorageGroupDestination = EditStorageGroupDestination(),
) {
    val presenter by rememberOnScreenRef { DI.editStorageGroupPresenter(dest.identifier()).apply { init() } }
    val router by LocalRouter.currentRef
    val groupNameFieldFocusRequester = remember { FocusRequester() }
    val state by presenter!!.state.collectAsState(key = Unit, initial = EditStorageGroupsState())

    val imeIsVisibleAnimated by animateTargetCrossFaded(WindowInsets.isIme)
    val safeContentPaddings = WindowInsets.safeContent.asPaddingValues()
    val isSaveAvailable by rememberTargetCrossFaded { state.isSaveAvailable }
    val isRemoveAvailable by rememberTargetCrossFaded { state.isRemoveAvailable }
    var dragProgress by remember { mutableFloatStateOf(0f) }

    SimpleBottomSheetScaffold(
        topContentSize = 250.dp,
        topMargin = AppBarConst.appBarSize + WindowInsets.safeContent.topDp,
        onDrag = { dragProgress = it },
        topContent = {
            EditGroupInfoContent(
                modifier = Modifier
                    .fillMaxHeight()
                    .alpha(dragProgress.topContentAlphaFromDrag())
                    .offset(y = dragProgress.topContentOffsetFromDrag()),
                groupNameFieldModifier = Modifier
                    .focusRequester(groupNameFieldFocusRequester),
                variants = state.colorGroupVariants,
                selectedId = state.selectedGroupId,
                groupName = if (state.isExternalGroupMode) state.extStorageName else state.name,
                favoriteVisible = !state.isExternalGroupMode,
                favoriteChecked = state.isFavorite,
                onChangeGroupName = {
                    if (state.isExternalGroupMode) {
                        presenter?.input { copy(extStorageName = it.take(3)) }
                    } else {
                        presenter?.input { copy(name = it.take(1)) }
                    }
                },
                onSelect = {
                    groupNameFieldFocusRequester.freeFocus()
                    presenter?.input { copy(selectedGroupId = it.id) }
                },
                onFavoriteChecked = {
                    presenter?.input { copy(isFavorite = it) }
                }

            )
        },
        sheetContent = {
            StorageSelectToGroupComponent(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxSize(),
                dest = dest,
                isSelectAvailable = !state.isExternalGroupMode,
                onSelect = { storagePath, selected ->
                    presenter?.selectStorage(storagePath, selected)
                },
                footer = {
                    Spacer(modifier = Modifier.height(safeContentPaddings.calculateBottomPadding() + 16.dp + ButtonDefaults.MinHeight + 16.dp))
                }
            )
        }
    )

    AppBarStates(
        modifier = Modifier,
        navigationIcon = {
            IconButton(onClick = remember { { router?.back() } }) {
                Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
            }
        },
        titleContent = { Text(text = stringResource(id = if (state.isEditMode) R.string.edit_storages_group else R.string.create_storages_group)) },
        actions = {
            if (isRemoveAvailable.current) {
                DeleteIconButton(
                    modifier = Modifier
                        .alpha(isRemoveAvailable.alpha),
                    onClick = { presenter?.remove() }
                )
            }

            if (imeIsVisibleAnimated.current && isSaveAvailable.current) {
                DoneIconButton(
                    modifier = Modifier.alpha(imeIsVisibleAnimated.alpha),
                    onClick = { presenter?.save() }
                )
            }
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = safeContentPaddings.calculateBottomPadding() + 16.dp)
            .padding(horizontal = safeContentPaddings.horizontal(minValue = 16.dp)),
    ) {
        if (!imeIsVisibleAnimated.current && isSaveAvailable.current) {
            FilledTonalButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .alpha(isSaveAvailable.alpha)
                    .fillMaxWidth(),
                onClick = { presenter?.save() }
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }
}


@OptIn(DebugOnly::class)
@VisibleForTesting
@Preview(device = Devices.PHONE)
@Composable
fun EditStorageGroupPreview() = EdgeToEdgeTemplate {
    AppTheme(theme = DefaultThemes.darkTheme) {
        DI.hardResetToPreview()
        DI.initPresenterModule(object : PresentersModule {
            override fun editStorageGroupPresenter(storageIdentifier: StorageGroupIdentifier) = object : EditStoragesGroupPresenterDummy() {
                override val state = MutableStateFlow(
                    EditStorageGroupsState(
                        isSkeleton = false,
                        isRemoveAvailable = false,
                        isSaveAvailable = true,
                    )
                )
            }
        })
        EditStorageGroupsScreen(dest = EditStorageGroupDestination(groupId = Dummy.dummyId))
    }
}


@OptIn(DebugOnly::class)
@VisibleForTesting
@Preview(device = Devices.PHONE)
@Composable
fun EditStorageGroupBigListPreview() = EdgeToEdgeTemplate {
    AppTheme(theme = DefaultThemes.darkTheme) {
        DI.hardResetToPreview()
        DI.initPresenterModule(object : PresentersModule {
            override fun editStorageGroupPresenter(storageIdentifier: StorageGroupIdentifier) = object : EditStoragesGroupPresenterDummy(storagesCount = 24) {
                override val state = MutableStateFlow(
                    EditStorageGroupsState(
                        isSkeleton = false,
                        isRemoveAvailable = false,
                        isSaveAvailable = true,
                    )
                )
            }
        })
        EditStorageGroupsScreen(dest = EditStorageGroupDestination(groupId = Dummy.dummyId))
    }
}

