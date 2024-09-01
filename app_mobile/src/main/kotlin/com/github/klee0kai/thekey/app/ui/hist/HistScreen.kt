@file:OptIn(ExperimentalFoundationApi::class)

package com.github.klee0kai.thekey.app.ui.hist

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.ui.hist.components.HistPasswItem
import com.github.klee0kai.thekey.app.ui.hist.components.popup.HistPasswPopup
import com.github.klee0kai.thekey.app.ui.hist.presenter.HistPresenterDummy
import com.github.klee0kai.thekey.app.ui.navigation.model.HistDestination
import com.github.klee0kai.thekey.app.ui.navigation.noteIdentifier
import com.github.klee0kai.thekey.app.ui.navigation.storageIdentifier
import com.github.klee0kai.thekey.app.ui.storage.model.SearchState
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarConst
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarStates
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.SearchField
import com.github.klee0kai.thekey.core.ui.devkit.components.settings.SectionHeader
import com.github.klee0kai.thekey.core.ui.devkit.icons.BackMenuIcon
import com.github.klee0kai.thekey.core.ui.devkit.overlay.PopupMenu
import com.github.klee0kai.thekey.core.ui.devkit.theme.DefaultThemes
import com.github.klee0kai.thekey.core.utils.possitions.onGlobalPositionState
import com.github.klee0kai.thekey.core.utils.possitions.rememberViewPosition
import com.github.klee0kai.thekey.core.utils.views.appBarVisible
import com.github.klee0kai.thekey.core.utils.views.collectAsState
import com.github.klee0kai.thekey.core.utils.views.currentRef
import com.github.klee0kai.thekey.core.utils.views.hideOnTargetAlpha
import com.github.klee0kai.thekey.core.utils.views.horizontal
import com.github.klee0kai.thekey.core.utils.views.ifProduction
import com.github.klee0kai.thekey.core.utils.views.rememberClick
import com.github.klee0kai.thekey.core.utils.views.rememberClickDebounced
import com.github.klee0kai.thekey.core.utils.views.rememberClickDebouncedArg
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef
import com.github.klee0kai.thekey.core.utils.views.rememberTargetCrossFaded
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import org.jetbrains.annotations.VisibleForTesting
import kotlin.time.Duration

private const val SearchTitleId = 0
private const val MainTitleId = 1

@Composable
fun GenHistScreen(
    dest: HistDestination = HistDestination(),
) {
    val router by LocalRouter.currentRef
    val safeContentPadding = WindowInsets.safeContent.asPaddingValues()
    val scrollState = rememberLazyListState()
    val appBarVisible by scrollState.appBarVisible()
    val noteIdentifier = remember(dest) { dest.noteIdentifier() }
    val presenter by rememberOnScreenRef {
        if (noteIdentifier != null) {
            DI.noteHistPresenter(noteIdentifier).apply { init() }
        } else {
            DI.genHistPresenter(dest.storageIdentifier()).apply { init() }
        }
    }
    val histList by presenter!!.filteredHist.collectAsState(key = Unit, initial = null)
    val searchState by presenter!!.searchState.collectAsState(key = Unit, initial = SearchState())
    val searchFocusRequester = remember { FocusRequester() }

    val targetTitleId by rememberTargetCrossFaded {
        when {
            searchState.isActive -> SearchTitleId
            else -> MainTitleId
        }
    }

    BackHandler(enabled = searchState.isActive) {
        presenter?.searchFilter(SearchState())
    }

    LazyColumn(
        state = scrollState,
        modifier = Modifier
            .fillMaxSize(),
    ) {
        item("start_spacer") {
            Spacer(
                modifier = Modifier
                    .ifProduction { animateItemPlacement() }
                    .height(safeContentPadding.calculateTopPadding() + AppBarConst.appBarSize)
            )
        }

        var oldChDate: String? = null
        histList?.forEach { hist ->
            if (!hist.changeDateStr.isNullOrBlank() && hist.changeDateStr != oldChDate) {
                oldChDate = hist.changeDateStr
                item("sec-${hist.changeDateStr}") {
                    SectionHeader(
                        modifier = Modifier
                            .ifProduction { animateItemPlacement() },
                        text = hist.changeDateStr ?: ""
                    )
                }
            }

            item(hist.histPtr) {
                var showMenu by remember { mutableStateOf(false) }
                val position = rememberViewPosition()

                HistPasswItem(
                    modifier = Modifier
                        .onGlobalPositionState(position)
                        .ifProduction { animateItemPlacement() }
                        .combinedClickable(
                            onClick = rememberClick(hist) {
                                showMenu = false
                                presenter?.savePassw(hist.histPtr, router)
                            },
                            onLongClick = rememberClick(hist) { showMenu = !showMenu },
                        )
                        .padding(horizontal = safeContentPadding.horizontal(minValue = 16.dp)),
                    passw = hist,
                )

                PopupMenu(
                    visible = showMenu,
                    positionAnchor = position,
                    horizontalBias = 0.8f,
                    onDismissRequest = rememberClickDebounced { showMenu = false }
                ) {
                    HistPasswPopup(
                        modifier = Modifier.padding(vertical = 4.dp),
                        onSave = if (noteIdentifier == null) rememberClickDebounced(hist) {
                            showMenu = false
                            presenter?.savePassw(hist.histPtr, router)
                        } else null,
                        onCopy = rememberClickDebounced(hist) {
                            showMenu = false
                            presenter?.copyPassw(hist.histPtr, router)
                        },
                        onRemove = rememberClickDebounced(hist) {
                            showMenu = false
                            presenter?.removePassw(hist.histPtr, router)
                        },
                    )
                }
            }
        }

        item("end_spacer") {
            Spacer(
                modifier = Modifier
                    .ifProduction { animateItemPlacement() }
                    .height(safeContentPadding.calculateBottomPadding() + 16.dp)
            )
        }
    }

    AppBarStates(
        isVisible = appBarVisible,
        navigationIcon = {
            IconButton(
                onClick = rememberClickDebounced { router?.back() },
                content = { BackMenuIcon() }
            )
        },
        titleContent = {
            when (targetTitleId.current) {
                MainTitleId -> {
                    Text(text = stringResource(id = R.string.gen_history))
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
        }
    )
}

@VisibleForTesting
@Preview(device = Devices.PHONE)
@Composable
fun GenHistScreenPreview() = EdgeToEdgeTemplate {
    AppTheme(theme = DefaultThemes.darkTheme) {
        DI.initPresenterModule(object : PresentersModule {
            override fun genHistPresenter(storageIdentifier: StorageIdentifier) =
                object : HistPresenterDummy(histCount = 30) {

                }
        })
        GenHistScreen()
    }
}