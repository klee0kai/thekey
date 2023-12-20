@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.github.klee0kai.thekey.app.ui.storage

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
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
import com.github.klee0kai.thekey.app.ui.designkit.components.SecondaryTabs
import com.github.klee0kai.thekey.app.ui.designkit.components.SecondaryTabsConst
import com.github.klee0kai.thekey.app.ui.designkit.components.rememberMainTitleVisibleFlow
import com.github.klee0kai.thekey.app.ui.designkit.components.rememberSimpleBottomSheetScaffoldState
import com.github.klee0kai.thekey.app.ui.storage.pages.AccountsPage
import com.github.klee0kai.thekey.app.ui.storage.pages.GeneratePasswordPage


@Preview(showBackground = true)
@Composable
fun StorageScreen(
    path: String = ""
) {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val presenter = remember { DI.loginPresenter() }
    val navigator = remember { DI.navigator() }
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
    val isAccountTab = pagerState.currentPage == 0

    val accountTitleVisibility = accountScaffoldState.rememberMainTitleVisibleFlow()
    val mainTitleVisibility = !isAccountTab || accountTitleVisibility.value
    val tabsVisible = !isAccountTab || accountScaffoldState.dragProgress.floatValue > 0.4f
    val tabsAlpha by animateFloatAsState(
        targetValue = if (tabsVisible) 1f else 0f,
        label = "tabs visible animate"
    )

    HorizontalPager(
        pagerState,
        modifier = Modifier
            .fillMaxSize(),
        beyondBoundsPageCount = titles.size, // fix bottomSheet
        pageContent = { page ->
            when (page) {
                0 -> AccountsPage(
                    scaffoldState = accountScaffoldState
                )

                1 -> GeneratePasswordPage(
                    modifier = Modifier.padding(top = AppBarConst.appBarSize)
                )
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
        mainTitleVisibility = mainTitleVisibility,
        navigationIcon = {
            IconButton(onClick = { backDispatcher?.onBackPressed() }) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = null,
                )
            }
        },
        appBarSticky = {
            Text(text = stringResource(id = R.string.accounts))
        },
    )


}