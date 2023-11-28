package com.github.klee0kai.thekey.app.ui.storages

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.designkit.components.SimpleAppBar
import dev.olshevski.navigation.reimagined.pop

@Preview
@Composable
fun StoragesScreen() {
    val scope = rememberCoroutineScope()
    val presenter = remember { DI.mainViewModule() }
    val navigator = remember { DI.navigator() }
    val context = LocalView.current.context

    Scaffold(
        topBar = { SimpleAppBar(backClick = { navigator.pop() }) },
        content = { padding ->
            ConstraintLayout(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                val (storagesList) = createRefs()

            }
        }
    )
}