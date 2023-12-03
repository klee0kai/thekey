package com.github.klee0kai.thekey.app.ui.storages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.model.Storage
import com.github.klee0kai.thekey.app.ui.designkit.components.SimpleAppBar
import dev.olshevski.navigation.reimagined.pop

@Preview
@Composable
fun StoragesScreen() {
    val scope = rememberCoroutineScope()
    val presenter = remember { DI.storagesPresenter() }
    val navigator = remember { DI.navigator() }
    val context = LocalView.current.context

    val storages = presenter.storages().collectAsState(initial = emptyList())

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

                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    storages.value.forEach { storage ->
                        item {
                            StorageItem(storage)
                        }
                    }
                }
            }
        }
    )
}


@Preview
@Composable
fun StorageItem(
    storage: Storage = Storage()
) {
    var storage = if (LocalView.current.isInEditMode) {
        Storage(path = "path", name = "name", description = "description")
    } else storage

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Color.White)
    ) {
        val (colorGroup, path, desctiption) = createRefs()

        Divider(
            color = Color.Blue,
            modifier = Modifier
                .fillMaxHeight()
                .width(3.dp)
                .constrainAs(colorGroup) {
                    start.linkTo(parent.start, 8.dp)
                    top.linkTo(parent.top, 2.dp)
                    bottom.linkTo(parent.bottom, 2.dp)
                }
        )


        Text(
            text = storage.path,
            modifier = Modifier
                .constrainAs(path) {
                    start.linkTo(colorGroup.end, 4.dp)
                }
        )

        Text(
            text = storage.description,
            modifier = Modifier
                .constrainAs(desctiption) {
                    start.linkTo(colorGroup.end, 4.dp)
                    top.linkTo(path.bottom)
                }
        )


    }


}