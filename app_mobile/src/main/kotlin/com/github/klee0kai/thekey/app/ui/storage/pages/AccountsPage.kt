@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.github.klee0kai.thekey.app.ui.storage.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.ui.designkit.components.AppBarConst
import com.github.klee0kai.thekey.app.ui.designkit.components.SimpleBottomSheetScaffold
import com.github.klee0kai.thekey.app.ui.designkit.components.SimpleBottomSheetScaffoldState
import com.github.klee0kai.thekey.app.ui.designkit.components.rememberSimpleBottomSheetScaffoldState

@Preview
@Composable
fun AccountsPage(
    scaffoldState: SimpleBottomSheetScaffoldState =
        rememberSimpleBottomSheetScaffoldState(
            topContentSize = 190.dp,
            appBarSize = AppBarConst.appBarSize
        )
) {

    SimpleBottomSheetScaffold(
        simpleBottomSheetScaffoldState = scaffoldState,
        topContent = {
            Box(modifier = Modifier.fillMaxSize()) {

            }
        },
        sheetContent = {
            Box(modifier = Modifier.fillMaxSize()) {

            }
        },
    )

}