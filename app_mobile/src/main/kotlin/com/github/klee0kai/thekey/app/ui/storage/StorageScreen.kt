package com.github.klee0kai.thekey.app.ui.storage

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.designkit.components.TabsBottomSheetScaffold
import com.github.klee0kai.thekey.app.ui.designkit.components.rememberTabsBottomSheetScaffoldState


@Preview(showBackground = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorageScreen(
    path: String = ""
) {
    val presenter = remember { DI.loginPresenter() }
    val navigator = remember { DI.navigator() }

    val scaffoldState = rememberTabsBottomSheetScaffoldState(
        titles = listOf("re", "re2")
    )
    TabsBottomSheetScaffold(
        topContentSize = 190.dp,
        scaffoldState = scaffoldState,

        )


}