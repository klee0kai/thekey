package com.github.klee0kai.thekey.app.ui.designkit

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.di.DI
import kotlinx.coroutines.launch


@Preview
@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
fun DesignScreen() {
    val scope = rememberCoroutineScope()
    val presenter = remember { DI.storagesPresenter() }
    val navigator = remember { DI.navigator() }

    val titles = listOf("Tab 1", "Tab 2", "Tab 3")
    val colors = listOf(Color.Cyan, Color.Green, Color.Red)
    val pagerState = rememberPagerState { 3 }


    SecondaryTabRow(
        selectedTabIndex = pagerState.currentPage,
    ) {
        titles.forEachIndexed { index, title ->
            val selected = pagerState.currentPage == index

            Tab(
                selected = selected,
                onClick = {
                    scope.launch {
                        pagerState.scrollToPage(index)
                    }
                }
            ) {
                Column(
                    Modifier
                        .padding(10.dp)
                        .height(30.dp)
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

    HorizontalPager(
        pagerState,
        modifier = Modifier
            .padding(top = 50.dp)
            .fillMaxSize()

    ) { page ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors[page])
        )

    }


}

