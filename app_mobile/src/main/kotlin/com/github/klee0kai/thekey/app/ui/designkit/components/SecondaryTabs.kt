@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.github.klee0kai.thekey.app.ui.designkit.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.R
import kotlinx.coroutines.launch

object SecondaryTabsConst {
    val textPadding = 10.dp
    val textHeight = 30.dp
    val topPadding = 26.dp
    val allHeight = textHeight + textPadding * 2 + topPadding
}

@Composable
@Preview
fun SecondaryTabs(
    modifier: Modifier = Modifier,
    titles: List<String> = listOf(
        stringResource(id = R.string.accounts),
        stringResource(id = R.string.passw_generate)
    ),
    pagerState: PagerState = rememberPagerState { titles.size }
) {
    val scope = rememberCoroutineScope()

    SecondaryTabRow(
        modifier = modifier
            .padding(top = AppBarConst.appBarSize)
            .background(MaterialTheme.colorScheme.background),
        selectedTabIndex = pagerState.currentPage,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) {
        titles.forEachIndexed { index, title ->
            val selected = pagerState.currentPage == index
            Tab(
                modifier = Modifier
                    .padding(top = SecondaryTabsConst.topPadding),
                selected = selected,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            ) {
                Column(
                    Modifier
                        .padding(SecondaryTabsConst.textPadding)
                        .height(SecondaryTabsConst.textHeight)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }

}