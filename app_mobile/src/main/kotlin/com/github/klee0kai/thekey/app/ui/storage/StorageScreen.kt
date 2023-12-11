package com.github.klee0kai.thekey.app.ui.storage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.designkit.components.SimpleBottomSheetScaffold

@Preview(showBackground = true)
@Composable
fun StorageScreen(
    path: String = ""
) {
    val presenter = remember { DI.loginPresenter() }
    val navigator = remember { DI.navigator() }


    SimpleBottomSheetScaffold(
        topContentSize = 190.dp,

        )


}