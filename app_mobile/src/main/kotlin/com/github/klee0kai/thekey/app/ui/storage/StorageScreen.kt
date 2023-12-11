package com.github.klee0kai.thekey.app.ui.storage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.github.klee0kai.thekey.app.di.DI

@Preview(showBackground = true)
@Composable
fun StorageScreen(
    path: String = ""
) {
    val presenter = remember { DI.loginPresenter() }
    val navigator = remember { DI.navigator() }



}