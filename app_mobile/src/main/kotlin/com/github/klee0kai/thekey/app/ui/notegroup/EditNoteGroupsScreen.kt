@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.klee0kai.thekey.app.ui.notegroup

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.hardResetToPreview
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.ui.navigation.identifier
import com.github.klee0kai.thekey.app.ui.navigation.model.EditNoteGroupDestination
import com.github.klee0kai.thekey.app.ui.notegroup.components.EditGroupInfoContent
import com.github.klee0kai.thekey.app.ui.notegroup.components.NoteSelectToGroupComponent
import com.github.klee0kai.thekey.app.ui.notegroup.model.EditNoteGroupsState
import com.github.klee0kai.thekey.app.ui.notegroup.presenter.EditNoteGroupsPresenterDummy
import com.github.klee0kai.thekey.app.ui.notegroup.presenter.selectStorageItem
import com.github.klee0kai.thekey.app.ui.storage.model.SearchState
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.di.identifiers.NoteGroupIdentifier
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.Screen
import com.github.klee0kai.thekey.core.ui.devkit.bottomsheet.SimpleBottomSheetScaffold
import com.github.klee0kai.thekey.core.ui.devkit.bottomsheet.topContentAlphaFromDrag
import com.github.klee0kai.thekey.core.ui.devkit.bottomsheet.topContentOffsetFromDrag
import com.github.klee0kai.thekey.core.ui.devkit.color.KeyColor
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarConst
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarStates
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.DeleteIconButton
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.DoneIconButton
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.SearchField
import com.github.klee0kai.thekey.core.ui.devkit.icons.BackMenuIcon
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.common.Dummy
import com.github.klee0kai.thekey.core.utils.views.DebugDarkScreenPreview
import com.github.klee0kai.thekey.core.utils.views.animateTargetCrossFaded
import com.github.klee0kai.thekey.core.utils.views.collectAsState
import com.github.klee0kai.thekey.core.utils.views.currentRef
import com.github.klee0kai.thekey.core.utils.views.hideOnTargetAlpha
import com.github.klee0kai.thekey.core.utils.views.isIme
import com.github.klee0kai.thekey.core.utils.views.rememberClickArg
import com.github.klee0kai.thekey.core.utils.views.rememberClickDebounced
import com.github.klee0kai.thekey.core.utils.views.rememberClickDebouncedArg
import com.github.klee0kai.thekey.core.utils.views.rememberClickDebouncedArg2
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef
import com.github.klee0kai.thekey.core.utils.views.rememberTargetCrossFaded
import kotlinx.coroutines.flow.MutableStateFlow
import org.jetbrains.annotations.VisibleForTesting
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

private const val SearchTitleId = 0
private const val MainTitleId = 1

@Composable
fun EditNoteGroupsScreen(
    dest: EditNoteGroupDestination = EditNoteGroupDestination(),
) = Screen {
    val router by LocalRouter.currentRef
    val theme = LocalTheme.current
    val safeContentPadding = WindowInsets.safeContent.asPaddingValues()
    val imeIsVisibleAnimated by animateTargetCrossFaded(WindowInsets.isIme)
    val isNavBoardOpen by router!!.isNavBoardOpen.collectAsState(key = Unit, initial = false)
    val presenter by rememberOnScreenRef {
        DI.editNoteGroupPresenter(dest.identifier()).apply { init() }
    }
    val searchFocusRequester = remember { FocusRequester() }
    val searchState by presenter!!.searchState.collectAsState(key = Unit, initial = SearchState())
    val groupNameFieldFocusRequester = remember { FocusRequester() }
    val state by presenter!!.state.collectAsState(key = Unit, initial = EditNoteGroupsState())
    val isSaveAvailable by rememberTargetCrossFaded { state.isSaveAvailable }
    val isRemoveAvailable by rememberTargetCrossFaded { state.isRemoveAvailable }
    var dragProgress by remember { mutableFloatStateOf(0f) }

    val targetTitleId by rememberTargetCrossFaded {
        when {
            searchState.isActive -> SearchTitleId
            else -> MainTitleId
        }
    }

    BackHandler(enabled = searchState.isActive || isNavBoardOpen) {
        when {
            isNavBoardOpen -> router?.hideNavigationBoard()
            searchState.isActive -> presenter?.searchFilter(SearchState())
        }
    }

    LaunchedEffect(key1 = targetTitleId.current) {
        if (targetTitleId.current == SearchTitleId) {
            searchFocusRequester.requestFocus()
        }
    }

    SimpleBottomSheetScaffold(
        topContentSize = 190.dp,
        topMargin = AppBarConst.appBarSize + safeContentPadding.calculateTopPadding(),
        onDrag = { dragProgress = it },
        topContent = {
            EditGroupInfoContent(
                modifier = Modifier
                    .fillMaxHeight()
                    .alpha(dragProgress.topContentAlphaFromDrag())
                    .offset(y = dragProgress.topContentOffsetFromDrag()),
                groupNameFieldModifier = Modifier
                    .focusRequester(groupNameFieldFocusRequester),
                selectedId = state.selectedGroupId,
                variants = state.colorGroupVariants,
                groupName = when {
                    state.isOtpGroupMode -> state.otpColorName
                    else -> state.name
                },
                onChangeGroupName = rememberClickArg {
                    if (state.isOtpGroupMode) {
                        presenter?.input { copy(otpColorName = it) }
                    } else {
                        presenter?.input { copy(name = it) }
                    }
                },
                onSelect = rememberClickDebouncedArg(debounce = 50.milliseconds) {
                    groupNameFieldFocusRequester.freeFocus()
                    presenter?.input { copy(selectedGroupId = it.id) }
                }
            )
        },
        sheetContent = {
            NoteSelectToGroupComponent(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxSize(),
                dest = dest,
                isOtpMode = state.isOtpGroupMode,
                onSelect = rememberClickDebouncedArg2(debounce = 100.milliseconds) { storageItemId, selected ->
                    presenter?.selectStorageItem(storageItemId, selected)
                },
                footer = { Spacer(modifier = Modifier.height(200.dp)) }
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
        titleContent = {
            when (targetTitleId.current) {
                MainTitleId -> {
                    Text(
                        modifier = Modifier.alpha(targetTitleId.alpha),
                        style = theme.typeScheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        text = stringResource(id = if (state.isEditMode) R.string.edit_group else R.string.create_storages_group),
                    )
                }

                SearchTitleId -> {
                    SearchField(
                        textModifier = Modifier
                            .focusRequester(searchFocusRequester),
                        searchText = searchState.searchText,
                        onSearch = rememberClickDebouncedArg(debounce = Duration.ZERO) { newText ->
                            presenter?.searchFilter(
                                SearchState(
                                    isActive = true,
                                    searchText = newText
                                )
                            )
                        },
                        onClose = rememberClickDebounced { presenter?.searchFilter(SearchState()) }
                    )
                }
            }
        },
        actions = {
            if (targetTitleId.current != SearchTitleId) {
                IconButton(
                    modifier = Modifier
                        .alpha(targetTitleId.hideOnTargetAlpha(SearchTitleId)),
                    onClick = rememberClickDebounced { presenter?.searchFilter(SearchState(isActive = true)) },
                    content = { Icon(Icons.Filled.Search, contentDescription = null) }
                )
            }

            if (isRemoveAvailable.current && targetTitleId.current != SearchTitleId) {
                DeleteIconButton(
                    modifier = Modifier
                        .alpha(isRemoveAvailable.alpha),
                    onClick = rememberClickDebounced { presenter?.remove(router) }
                )
            }
            if (imeIsVisibleAnimated.current
                && isSaveAvailable.current
                && targetTitleId.current != SearchTitleId
            ) {
                DoneIconButton(
                    modifier = Modifier.alpha(imeIsVisibleAnimated.alpha),
                    onClick = rememberClickDebounced { presenter?.save(router) }
                )
            }
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeContent)
            .padding(
                top = 16.dp + AppBarConst.appBarSize,
                bottom = 16.dp,
                start = 16.dp,
                end = 16.dp
            ),
    ) {
        if (!imeIsVisibleAnimated.current && isSaveAvailable.current) {
            FilledTonalButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .alpha(isSaveAvailable.alpha)
                    .fillMaxWidth(),
                onClick = rememberClickDebounced { presenter?.save(router) }
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
fun EditNoteGroupsSkeletonPreview() {
    DI.hardResetToPreview()
    DI.initPresenterModule(object : PresentersModule {
        override fun editNoteGroupPresenter(id: NoteGroupIdentifier) =
            object : EditNoteGroupsPresenterDummy() {
                override val state = MutableStateFlow(
                    EditNoteGroupsState(
                        isSkeleton = true,
                        isEditMode = false,
                        colorGroupVariants = KeyColor.selectableColorGroups,
                    )
                )
            }
    })

    DebugDarkScreenPreview {
        EditNoteGroupsScreen(dest = EditNoteGroupDestination(groupId = Dummy.dummyId))
    }
}
