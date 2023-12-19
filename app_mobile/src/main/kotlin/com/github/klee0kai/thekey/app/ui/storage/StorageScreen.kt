@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.github.klee0kai.thekey.app.ui.storage

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.designkit.components.AppBarConst
import com.github.klee0kai.thekey.app.ui.designkit.components.AppBarStates
import com.github.klee0kai.thekey.app.ui.designkit.components.SecondaryTabs
import com.github.klee0kai.thekey.app.ui.designkit.components.SecondaryTabsConst
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
            topContentSize = 190.dp,
            appBarSize = AppBarConst.appBarSize + SecondaryTabsConst.allHeight
        )

    HorizontalPager(
        pagerState,
        modifier = Modifier
            .padding(top = AppBarConst.appBarSize)
            .fillMaxSize(),
        beyondBoundsPageCount = titles.size, // fix bottomSheet
        pageContent = { page ->
            when (page) {
                0 -> AccountsPage(accountScaffoldState)
                1 -> GeneratePasswordPage()
            }
        }
    )

    SecondaryTabs(
        titles = titles,
        pagerState = pagerState
    )

    AppBarStates(
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