@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.github.klee0kai.thekey.app.ui.storage

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.designkit.components.AppBarConst
import com.github.klee0kai.thekey.app.ui.designkit.components.AppBarStates
import com.github.klee0kai.thekey.app.ui.designkit.components.AppTitleImage
import com.github.klee0kai.thekey.app.ui.designkit.components.SearchField
import com.github.klee0kai.thekey.app.ui.designkit.components.SecondaryTabs
import com.github.klee0kai.thekey.app.ui.designkit.components.SecondaryTabsConst
import com.github.klee0kai.thekey.app.ui.designkit.components.bottomsheet.simpleBottomSheetScaffoldState
import com.github.klee0kai.thekey.app.ui.designkit.components.rememberMainTitleVisibleFlow
import com.github.klee0kai.thekey.app.ui.navigation.LocalRouter
import com.github.klee0kai.thekey.app.ui.navigation.identifier
import com.github.klee0kai.thekey.app.ui.navigation.model.StorageDestination
import com.github.klee0kai.thekey.app.ui.storage.genpassw.GenPasswordContent
import com.github.klee0kai.thekey.app.ui.storage.model.SearchState
import com.github.klee0kai.thekey.app.ui.storage.notes.NotesContent
import com.github.klee0kai.thekey.app.utils.views.animateAlphaAsState
import com.github.klee0kai.thekey.app.utils.views.collectAsState
import com.github.klee0kai.thekey.app.utils.views.hideAlpha
import com.github.klee0kai.thekey.app.utils.views.rememberAlphaAnimate
import com.github.klee0kai.thekey.app.utils.views.rememberDerivedStateOf
import com.github.klee0kai.thekey.app.utils.views.rememberTargetAlphaCrossSade

private const val SearchTitleId = 0
private const val MainTitleId = 1
private const val SecondTittleId = 2


@Preview(showBackground = true)
@Composable
fun StorageScreen(
    args: StorageDestination = StorageDestination()
) {
    val presenter = remember { DI.storagePresenter(args.identifier()) }
    val navigator = LocalRouter.current
    val density = LocalDensity.current
    val titles = listOf(
        stringResource(id = R.string.accounts),
        stringResource(id = R.string.passw_generate)
    )
    val searchFocusRequester = remember { FocusRequester() }
    val searchState by presenter.searchState.collectAsState(Unit)
    val dragProgress = remember { mutableFloatStateOf(0f) }
    val pagerState = rememberPagerState(initialPage = args.selectedPage.coerceIn(titles.indices)) { titles.size }
    val singlePagePagerState = rememberPagerState(initialPage = 0) { 1 }
    val pagerStateFiltered by rememberDerivedStateOf { if (searchState.isActive) singlePagePagerState else pagerState }
    val secondaryTabsHeight by rememberDerivedStateOf { if (searchState.isActive) 0.dp else SecondaryTabsConst.allHeight }
    val accountScaffoldState by rememberDerivedStateOf {
        simpleBottomSheetScaffoldState(
            density = density,
            topContentSize = secondaryTabsHeight + 170.dp,
            appBarSize = AppBarConst.appBarSize
        )
    }
    val isAccountTab by rememberDerivedStateOf { pagerState.currentPage == 0 && pagerState.currentPageOffsetFraction == 0f }
    val isAccountPageAlpha by animateAlphaAsState(boolean = isAccountTab)
    val accountTitleVisibility = accountScaffoldState.rememberMainTitleVisibleFlow()
    val tabsAlpha by rememberAlphaAnimate {
        when {
            searchState.isActive -> false
            !isAccountTab -> true
            dragProgress.floatValue > 0.4f -> true
            else -> false
        }
    }
    val targetTitleId by rememberTargetAlphaCrossSade {
        when {
            searchState.isActive -> SearchTitleId
            !isAccountTab || accountTitleVisibility.value == true -> MainTitleId
            else -> SecondTittleId
        }
    }

    BackHandler(enabled = searchState.isActive) {
        presenter.filterNotes(SearchState())
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
                            .animateContentSize()
                            .padding(top = secondaryTabsHeight),
                        onDrag = { dragProgress.value = it },
                        args = args,
                        isPageFullyAvailable = isAccountTab && !searchState.isActive,
                        scaffoldState = accountScaffoldState
                    )

                    1 -> GenPasswordContent(
                        modifier = Modifier.padding(top = AppBarConst.appBarSize + SecondaryTabsConst.allHeight),
                        dest = args,
                    )
                }
            }
        }
    )

    if (tabsAlpha > 0f) {
        SecondaryTabs(
            modifier = Modifier.alpha(tabsAlpha),
            titles = titles,
            pagerState = pagerState
        )
    }

    AppBarStates(
        navigationIcon = {
            IconButton(onClick = { navigator.back() }) {
                Icon(
                    Icons.Filled.ArrowBack,
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
                        text = stringResource(id = R.string.accounts)
                    )
                }

                SearchTitleId -> {
                    SearchField(
                        textModifier = Modifier
                            .focusRequester(searchFocusRequester),
                        searchText = searchState.searchText,
                        onSearch = { newText -> presenter.filterNotes(SearchState(isActive = true, searchText = newText)) },
                        onClose = { presenter.filterNotes(SearchState()) }
                    )
                }
            }
        },
        actions = {
            if (isAccountPageAlpha > 0 && targetTitleId.current != SearchTitleId) {
                IconButton(
                    modifier = Modifier
                        .alpha(targetTitleId.hideAlpha(SearchTitleId))
                        .alpha(isAccountPageAlpha),
                    onClick = { presenter.filterNotes(SearchState(isActive = true)) },
                    content = { Icon(Icons.Filled.Search, contentDescription = null) }
                )
            }
        }
    )


}