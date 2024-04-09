@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.github.klee0kai.thekey.app.ui.designkit.components.appbar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.ui.designkit.AppTheme
import com.github.klee0kai.thekey.app.utils.views.animateAlphaAsState
import com.github.klee0kai.thekey.app.utils.views.rememberDerivedStateOf

object SecondaryTabsConst {
    val topPadding = 26.dp
    val textHeight = 48.dp
    val allHeight = topPadding + textHeight   // should be 74
}

@Composable
fun SecondaryTabs(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    titles: List<String> = listOf(
        stringResource(id = R.string.accounts),
        stringResource(id = R.string.passw_generate)
    ),
    selectedTab: Int = 0,
    onTabClicked: (Int) -> Unit = { },
) {
    val appBarAlpha by animateAlphaAsState(isVisible)
    val isNotVisible by rememberDerivedStateOf { appBarAlpha <= 0 }
    if (isNotVisible) return

    SecondaryTabRow(
        modifier = modifier
            .padding(top = SecondaryTabsConst.topPadding)
            .alpha(appBarAlpha)
            .background(MaterialTheme.colorScheme.background),
        selectedTabIndex = selectedTab,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        indicator = {
            Box(
                Modifier
                    .tabIndicatorOffset(
                        selectedTabIndex = selectedTab,
                        matchContentSize = true
                    )
                    .height(2.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(1.dp),
                    )
            )
        }
    ) {
        titles.forEachIndexed { index, title ->
            val selected = selectedTab == index
            Tab(
                selected = selected,
                onClick = { onTabClicked(index) },
            ) {
                Box(
                    modifier = Modifier
                        .height(SecondaryTabsConst.textHeight)
                        .padding(start = 30.dp, end = 30.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = title,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun SecondaryTabsPreview() {
    AppTheme {
        SecondaryTabs(
            modifier = Modifier
                .padding(top = AppBarConst.appBarSize),
            isVisible = true,
            titles = listOf("Title1", "Title2"),
            selectedTab = 0,
        )

        AppBarStates(
            isVisible = true,
            navigationIcon = {
                IconButton(onClick = { }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = null)
                }
            },
            titleContent = { Text(text = stringResource(id = R.string.edit)) },
        )
    }
}