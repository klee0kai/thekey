package com.github.klee0kai.thekey.app.ui.storage

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.designkit.components.TabsScaffold


@Preview(showBackground = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorageScreen(
    path: String = ""
) {
    val presenter = remember { DI.loginPresenter() }
    val navigator = remember { DI.navigator() }

    TabsScaffold(

        )


}