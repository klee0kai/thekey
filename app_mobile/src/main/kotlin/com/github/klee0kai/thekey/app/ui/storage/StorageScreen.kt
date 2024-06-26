@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.github.klee0kai.thekey.app.ui.storage

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalDensity
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
import com.github.klee0kai.thekey.app.ui.navigation.model.StorageDestination
import com.github.klee0kai.thekey.app.ui.storage.genpassw.GenPasswordContent
import com.github.klee0kai.thekey.app.ui.storage.model.SearchState
import com.github.klee0kai.thekey.app.ui.storage.notes.NotesContent
import com.github.klee0kai.thekey.app.ui.storage.presenter.StoragePresenterDummy
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalAppConfig
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarConst
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarStates
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppTitleImage
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.SearchField
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.SecondaryTabs
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.SecondaryTabsConst
import com.github.klee0kai.thekey.core.ui.devkit.theme.DefaultThemes
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.common.Dummy
import com.github.klee0kai.thekey.core.utils.views.accumulate
import com.github.klee0kai.thekey.core.utils.views.animateAlphaAsState
import com.github.klee0kai.thekey.core.utils.views.collectAsState
import com.github.klee0kai.thekey.core.utils.views.hideOnTargetAlpha
import com.github.klee0kai.thekey.core.utils.views.minInsets
import com.github.klee0kai.thekey.core.utils.views.rememberAlphaAnimate
import com.github.klee0kai.thekey.core.utils.views.rememberDerivedStateOf
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef
import com.github.klee0kai.thekey.core.utils.views.rememberTargetCrossFaded
import com.github.klee0kai.thekey.core.utils.views.topDp
import com.github.klee0kai.thekey.core.utils.views.truncate
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import kotlinx.coroutines.launch
import org.jetbrains.annotations.VisibleForTesting

private const val SearchTitleId = 0
private const val MainTitleId = 1
private const val SecondTittleId = 2

@Composable
fun StorageScreen(
    dest: StorageDestination = StorageDestination()
) {
    val isEditMode = LocalAppConfig.current.isViewEditMode
    val presenter by rememberOnScreenRef { DI.storagePresenter(dest.identifier()) }
    val scope = rememberCoroutineScope()
    val router = LocalRouter.current
    val density = LocalDensity.current
    val titles = listOf(
        stringResource(id = R.string.accounts),
        stringResource(id = R.string.passw_generate)
    )
    val searchFocusRequester = remember { FocusRequester() }
    val searchState by presenter!!.searchState.collectAsState(key = Unit, initial = SearchState())
    var dragProgress by remember { mutableFloatStateOf(0f) }
    val pagerState = rememberPagerState(initialPage = dest.selectedPage.coerceIn(titles.indices)) { titles.size }
    val singlePagePagerState = rememberPagerState(initialPage = 0) { 1 }
    val pagerStateFiltered by rememberDerivedStateOf { if (searchState.isActive) singlePagePagerState else pagerState }
    val secondaryTabsHeight by rememberDerivedStateOf { if (searchState.isActive) 0.dp else SecondaryTabsConst.allHeight }
    val isAccountTab by rememberDerivedStateOf { pagerState.currentPage == 0 && pagerState.currentPageOffsetFraction == 0f }
    val isAccountPageAlpha by animateAlphaAsState(boolean = isAccountTab)
    val accountTitleVisibility by accumulate<Boolean?>(init = null) { old ->
        when {
            dragProgress < 0.1 -> false
            dragProgress > 0.3 -> true
            else -> old
        }
    }
    val tabsAlpha by rememberAlphaAnimate {
        when {
            searchState.isActive -> false
            !isAccountTab -> true
            dragProgress > 0.4f || isEditMode -> true
            else -> false
        }
    }
    val targetTitleId by rememberTargetCrossFaded {
        when {
            searchState.isActive -> SearchTitleId
            !isAccountTab || accountTitleVisibility == true -> MainTitleId
            else -> SecondTittleId
        }
    }

    BackHandler(enabled = searchState.isActive || router.isNavigationBoardIsOpen()) {
        when {
            router.isNavigationBoardIsOpen() -> scope.launch { router.hideNavigationBoard() }
            searchState.isActive -> presenter?.searchFilter(SearchState())
        }
    }

    LaunchedEffect(key1 = targetTitleId.current) {
        if (targetTitleId.current == SearchTitleId) {
            searchFocusRequester.requestFocus()
        }
    }

    HorizontalPager(
        state = pagerStateFiltered,
        modifier = Modifier
            .fillMaxSize(),
        pageContent = { page ->
            Box {
                when (page) {
                    0 -> NotesContent(
                        modifier = Modifier
                            .animateContentSize(),
                        secondaryTabsHeight = secondaryTabsHeight,
                        onDrag = { dragProgress = it },
                        dest = dest,
                        isPageFullyAvailable = isAccountTab && !searchState.isActive,
                    )

                    1 -> GenPasswordContent(
                        modifier = Modifier
                            .windowInsetsPadding(
                                WindowInsets.safeContent
                                    .minInsets(16.dp)
                                    .truncate(top = true)
                            )
                            .padding(
                                top = WindowInsets.safeContent.topDp + AppBarConst.appBarSize // system bar + app bar
                                        + SecondaryTabsConst.allHeight // tabs
                                        + 16.dp // space
                            ),
                        dest = dest,
                    )
                }
            }
        }
    )

    if (tabsAlpha > 0f) {
        SecondaryTabs(
            modifier = Modifier
                .padding(top = AppBarConst.appBarSize + WindowInsets.safeContent.topDp)
                .alpha(tabsAlpha),
            titles = titles,
            selectedTab = pagerState.currentPage,
            onTabClicked = { scope.launch { pagerState.animateScrollToPage(it) } },
        )
    }

    AppBarStates(
        navigationIcon = {
            IconButton(onClick = { scope.launch { router.showNavigationBoard() } }) {
                Icon(
                    Icons.Filled.Menu,
                    contentDescription = null,
                )
            }
        },
        titleContent = {
            when (targetTitleId.current) {
                MainTitleId -> AppTitleImage(modifier = Modifier.alpha(targetTitleId.alpha))
                SecondTittleId -> {
                    Text(
                        modifier = Modifier.alpha(targetTitleId.alpha),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        text = stringResource(id = R.string.accounts)
                    )
                }

                SearchTitleId -> {
                    SearchField(
                        textModifier = Modifier
                            .focusRequester(searchFocusRequester),
                        searchText = searchState.searchText,
                        onSearch = { newText -> presenter?.searchFilter(SearchState(isActive = true, searchText = newText)) },
                        onClose = { presenter?.searchFilter(SearchState()) }
                    )
                }
            }
        },
        actions = {
            if (isAccountPageAlpha > 0 && targetTitleId.current != SearchTitleId) {
                IconButton(
                    modifier = Modifier
                        .alpha(targetTitleId.hideOnTargetAlpha(SearchTitleId))
                        .alpha(isAccountPageAlpha),
                    onClick = { presenter?.searchFilter(SearchState(isActive = true)) },
                    content = { Icon(Icons.Filled.Search, contentDescription = null) }
                )
            }
        }
    )
}

@OptIn(DebugOnly::class)
@VisibleForTesting
@Preview(device = Devices.PHONE)
@Composable
fun StorageScreenAccountsPreview() = EdgeToEdgeTemplate {
    AppTheme(theme = DefaultThemes.darkTheme) {
        DI.hardResetToPreview()
        DI.initPresenterModule(object : PresentersModule {
            override fun storagePresenter(storageIdentifier: StorageIdentifier) = StoragePresenterDummy()
        })
        StorageScreen(
            dest = StorageDestination(path = Dummy.unicString, version = 2)
        )
    }
}

@OptIn(DebugOnly::class)
@VisibleForTesting
@Preview(device = Devices.PHONE)
@Composable
fun StorageScreenAccountsSearchPreview() = EdgeToEdgeTemplate {
    AppTheme(theme = DefaultThemes.darkTheme) {
        DI.hardResetToPreview()
        DI.initPresenterModule(object : PresentersModule {
            override fun storagePresenter(storageIdentifier: StorageIdentifier) =
                StoragePresenterDummy(isSearchActive = true)
        })
        StorageScreen(
            dest = StorageDestination(path = Dummy.unicString, version = 2)
        )
    }
}

@OptIn(DebugOnly::class)
@VisibleForTesting
@Preview(device = Devices.PHONE)
@Composable
fun StorageScreenGeneratePreview() = EdgeToEdgeTemplate {
    AppTheme(theme = DefaultThemes.darkTheme) {
        DI.hardResetToPreview()
        DI.initPresenterModule(object : PresentersModule {
            override fun storagePresenter(storageIdentifier: StorageIdentifier) = StoragePresenterDummy()
        })
        StorageScreen(
            dest = StorageDestination(path = Dummy.unicString, version = 2, selectedPage = 1)
        )
    }
}
