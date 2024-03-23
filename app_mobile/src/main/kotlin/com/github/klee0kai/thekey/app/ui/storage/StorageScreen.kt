@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.github.klee0kai.thekey.app.ui.storage

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.designkit.components.AppBarConst
import com.github.klee0kai.thekey.app.ui.designkit.components.AppBarStates
import com.github.klee0kai.thekey.app.ui.designkit.components.AppTitleImage
import com.github.klee0kai.thekey.app.ui.designkit.components.SecondaryTabs
import com.github.klee0kai.thekey.app.ui.designkit.components.SecondaryTabsConst
import com.github.klee0kai.thekey.app.ui.designkit.components.rememberMainTitleVisibleFlow
import com.github.klee0kai.thekey.app.ui.designkit.components.rememberSimpleBottomSheetScaffoldState
import com.github.klee0kai.thekey.app.ui.navigation.LocalRouter
import com.github.klee0kai.thekey.app.ui.navigation.model.StorageDestination
import com.github.klee0kai.thekey.app.ui.navigation.toStorageIdentifier
import com.github.klee0kai.thekey.app.ui.storage.genpassw.GenPasswordContent
import com.github.klee0kai.thekey.app.ui.storage.notes.NotesContent
import com.github.klee0kai.thekey.app.utils.views.animateAlphaAsState
import com.github.klee0kai.thekey.app.utils.views.rememberDerivedStateOf

private const val MainTitleId = 0
private const val SecondTittleId = 1


@Preview(showBackground = true)
@Composable
fun StorageScreen(
    args: StorageDestination = StorageDestination()
) {
    val presenter = remember { DI.storagePresenter(args.toStorageIdentifier()) }
    val navigator = LocalRouter.current
    val titles = listOf(
        stringResource(id = R.string.accounts),
        stringResource(id = R.string.passw_generate)
    )
    val pagerState = rememberPagerState { titles.size }
    val accountScaffoldState =
        rememberSimpleBottomSheetScaffoldState(
            topContentSize = SecondaryTabsConst.allHeight + 190.dp,
            appBarSize = AppBarConst.appBarSize
        )
    val isAccountTab by rememberDerivedStateOf {
        pagerState.currentPage == 0 && pagerState.currentPageOffsetFraction == 0f
    }

    val accountTitleVisibility = accountScaffoldState.rememberMainTitleVisibleFlow()
    val mainTitleVisibility by rememberDerivedStateOf { !isAccountTab || accountTitleVisibility.value == true }
    val tabsVisible by rememberDerivedStateOf { !isAccountTab || accountScaffoldState.dragProgress.floatValue > 0.4f }
    val targetTitleId = rememberDerivedStateOf { if (mainTitleVisibility) MainTitleId else SecondTittleId }
    val tabsAlpha by animateAlphaAsState(tabsVisible)

    HorizontalPager(
        pagerState,
        modifier = Modifier
            .fillMaxSize(),
        pageContent = { page ->
            Box {
                when (page) {
                    0 -> NotesContent(
                        args = args,
                        isPageFullyAvailable = isAccountTab,
                        scaffoldState = accountScaffoldState
                    )

                    1 -> GenPasswordContent(
                        modifier = Modifier.padding(top = AppBarConst.appBarSize + SecondaryTabsConst.allHeight),
                        args = args,
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
        titleId = targetTitleId,
        navigationIcon = {
            IconButton(onClick = { navigator.back() }) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = null,
                )
            }
        },
        titleContent = { titleId ->
            when (titleId) {
                MainTitleId -> AppTitleImage()
                SecondTittleId -> Text(text = stringResource(id = R.string.accounts))
            }
        },
    )


}