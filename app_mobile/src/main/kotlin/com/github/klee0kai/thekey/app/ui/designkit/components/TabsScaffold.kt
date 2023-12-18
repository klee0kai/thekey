package com.github.klee0kai.thekey.app.ui.designkit.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TabsScaffold(
    navigationIcon: (@Composable () -> Unit)? = null,
) {
    val scope = rememberCoroutineScope()
    val presenter = remember { DI.storagesPresenter() }
    val navigator = remember { DI.navigator() }
    val colorScheme = remember { DI.theme().colorScheme().androidColorScheme }

    val titles = listOf("Tab 1", "Tab 2", "Tab 3")
    val colors = listOf(Color.Cyan, Color.Green, Color.Red)
    val pagerState = rememberPagerState { 3 }


    Column {
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(containerColor = colorScheme.background),
            title = { AppLabelTitle() },
            navigationIcon = navigationIcon ?: {},
        )

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
                            .height(60.dp)
                            .fillMaxWidth(),

                        verticalArrangement = Arrangement.Center
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
                .fillMaxSize()

        ) { page ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors[page])
            )
        }
    }

}





