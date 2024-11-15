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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
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
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.bottomsheet.SimpleBottomSheetScaffold
import com.github.klee0kai.thekey.core.ui.devkit.bottomsheet.topContentAlphaFromDrag
import com.github.klee0kai.thekey.core.ui.devkit.bottomsheet.topContentOffsetFromDrag
import com.github.klee0kai.thekey.core.ui.devkit.color.KeyColor
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarConst
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarStates
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.DeleteIconButton
import com.github.klee0kai.thekey.core.ui.devkit.icons.BackMenuIcon
import com.github.klee0kai.thekey.core.ui.devkit.theme.DefaultThemes
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.common.Dummy
import com.github.klee0kai.thekey.core.utils.views.collectAsState
import com.github.klee0kai.thekey.core.utils.views.currentRef
import com.github.klee0kai.thekey.core.utils.views.horizontal
import com.github.klee0kai.thekey.core.utils.views.rememberClickDebounced
import com.github.klee0kai.thekey.core.utils.views.rememberClickDebouncedArg
import com.github.klee0kai.thekey.core.utils.views.rememberClickDebouncedArg2
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef
import com.github.klee0kai.thekey.core.utils.views.rememberTargetFaded
import com.github.klee0kai.thekey.core.utils.views.topDp
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import kotlinx.coroutines.flow.MutableStateFlow
import org.jetbrains.annotations.VisibleForTesting
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun EditStorageGroupsScreen(
    dest: EditStorageGroupDestination = EditStorageGroupDestination(),
) {
    val theme = LocalTheme.current
    val router by LocalRouter.currentRef

    val presenter by rememberOnScreenRef {
        DI.editStorageGroupPresenter(dest.identifier()).apply { init() }
    }
    val groupNameFieldFocusRequester = remember { FocusRequester() }
    val state by presenter!!.state.collectAsState(key = Unit, initial = EditStorageGroupsState())

    val safeContentPaddings = WindowInsets.safeContent.asPaddingValues()
    val isSaveAvailable by rememberTargetFaded { state.isSaveAvailable }
    val isRemoveAvailable by rememberTargetFaded { state.isRemoveAvailable }
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
                        presenter?.input { copy(extStorageName = it) }
                    } else {
                        presenter?.input { copy(name = it) }
                    }
                },
                onSelect = rememberClickDebouncedArg(debounce = 50.milliseconds) {
                    groupNameFieldFocusRequester.freeFocus()
                    presenter?.input { copy(selectedGroupId = it.id) }
                },
                onFavoriteChecked = rememberClickDebouncedArg(debounce = 50.milliseconds) {
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
                onSelect = rememberClickDebouncedArg2(debounce = 50.milliseconds) { storagePath, selected ->
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
            IconButton(
                onClick = rememberClickDebounced { router?.back() },
                content = { BackMenuIcon() },
            )
        },
        titleContent = { Text(text = stringResource(id = if (state.isEditMode) R.string.edit_storages_group else R.string.create_storages_group)) },
        actions = {
            if (isRemoveAvailable.current) {
                DeleteIconButton(
                    modifier = Modifier
                        .alpha(isRemoveAvailable.alpha),
                    onClick = rememberClickDebounced { presenter?.remove(router) }
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
        if (isSaveAvailable.current) {
            FilledTonalButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                onClick = rememberClickDebounced { presenter?.save(router) }
            ) {
                Text(
                    text = stringResource(R.string.save),
                    style = theme.typeScheme.buttonText,
                )
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
            override fun editStorageGroupPresenter(storageIdentifier: StorageGroupIdentifier) =
                object : EditStoragesGroupPresenterDummy() {
                    override val state = MutableStateFlow(
                        EditStorageGroupsState(
                            isSkeleton = false,
                            isRemoveAvailable = false,
                            isSaveAvailable = true,
                            colorGroupVariants = KeyColor.selectableColorGroups,
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
            override fun editStorageGroupPresenter(storageIdentifier: StorageGroupIdentifier) =
                object : EditStoragesGroupPresenterDummy(storagesCount = 24) {
                    override val state = MutableStateFlow(
                        EditStorageGroupsState(
                            isSkeleton = false,
                            isRemoveAvailable = false,
                            isSaveAvailable = true,
                            colorGroupVariants = KeyColor.selectableColorGroups,
                        )
                    )
                }
        })
        EditStorageGroupsScreen(dest = EditStorageGroupDestination(groupId = Dummy.dummyId))
    }
}

