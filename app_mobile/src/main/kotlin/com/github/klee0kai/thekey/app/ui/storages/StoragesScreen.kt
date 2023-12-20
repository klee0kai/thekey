package com.github.klee0kai.thekey.app.ui.storages

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.designkit.components.AppBarConst
import com.github.klee0kai.thekey.app.ui.designkit.components.AppBarStates
import com.github.klee0kai.thekey.app.ui.designkit.components.FabSimple
import com.github.klee0kai.thekey.app.ui.designkit.components.SimpleBottomSheetScaffold
import com.github.klee0kai.thekey.app.ui.designkit.components.rememberMainTitleVisibleFlow
import com.github.klee0kai.thekey.app.ui.designkit.components.rememberSimpleBottomSheetScaffoldState
import com.github.klee0kai.thekey.app.ui.storages.components.GroupsSelectContainer
import com.github.klee0kai.thekey.app.ui.storages.components.StoragesListContent

private val TOP_CONTENT_SIZE = 190.dp

@Preview
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun StoragesScreen() {
    val presenter = remember { DI.storagesPresenter() }
    val scaffoldState = rememberSimpleBottomSheetScaffoldState(
        topContentSize = TOP_CONTENT_SIZE,
        appBarSize = AppBarConst.appBarSize
    )
    val mainTitleVisibility = scaffoldState.rememberMainTitleVisibleFlow()
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    SimpleBottomSheetScaffold(
        simpleBottomSheetScaffoldState = scaffoldState,
        topContent = {
            GroupsSelectContainer(
                scaffoldState = scaffoldState
            )
        },
        sheetContent = {
            StoragesListContent(
                showStoragesTitle = scaffoldState.dragProgress.floatValue > 0.1f,
                modifier = Modifier.fillMaxSize()
            )
        },
        fab = {
            FabSimple(
                onClick = { }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        },
    )

    AppBarStates(
        mainTitleVisibility = mainTitleVisibility.value,
        navigationIcon = {
            IconButton(onClick = { backDispatcher?.onBackPressed() }) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = null,
                )
            }
        },
        appBarSticky = { Text(text = stringResource(id = R.string.storages)) },
    )


}

