package com.github.klee0kai.thekey.app.ui.storages

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.designkit.components.AppBarConst
import com.github.klee0kai.thekey.app.ui.designkit.components.AppBarStates
import com.github.klee0kai.thekey.app.ui.designkit.components.AppTitleImage
import com.github.klee0kai.thekey.app.ui.designkit.components.FabSimpleInContainer
import com.github.klee0kai.thekey.app.ui.designkit.components.SimpleBottomSheetScaffold
import com.github.klee0kai.thekey.app.ui.designkit.components.rememberMainTitleVisibleFlow
import com.github.klee0kai.thekey.app.ui.designkit.components.rememberSimpleBottomSheetScaffoldState
import com.github.klee0kai.thekey.app.ui.navigation.LocalRouter
import com.github.klee0kai.thekey.app.ui.navigation.model.EditStorageDestination
import com.github.klee0kai.thekey.app.ui.storages.components.GroupsSelectContent
import com.github.klee0kai.thekey.app.ui.storages.components.StoragesListContent
import com.github.klee0kai.thekey.app.utils.views.rememberDerivedStateOf

private val TOP_CONTENT_SIZE = 190.dp

private const val MainTitleId = 0
private const val SecondTittleId = 1

@Preview
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun StoragesScreen() {
    val presenter = remember { DI.storagesPresenter() }
    val router = LocalRouter.current
    val scaffoldState = rememberSimpleBottomSheetScaffoldState(
        topContentSize = TOP_CONTENT_SIZE,
        appBarSize = AppBarConst.appBarSize
    )
    val mainTitleVisibility by scaffoldState.rememberMainTitleVisibleFlow()
    val targetTitleId = rememberDerivedStateOf {
        when (mainTitleVisibility) {
            true -> MainTitleId
            false -> SecondTittleId
            null -> 0
        }
    }
    val showStoragesTitle by rememberDerivedStateOf { scaffoldState.dragProgress.floatValue > 0.1f }


    SideEffect {
        presenter.startup()
    }

    SimpleBottomSheetScaffold(
        simpleBottomSheetScaffoldState = scaffoldState,
        topContent = {
            GroupsSelectContent(scaffoldState = scaffoldState)
        },
        sheetContent = {
            StoragesListContent(
                modifier = Modifier.fillMaxSize(),
                showStoragesTitle = showStoragesTitle,
            )
        }
    )

    AppBarStates(
        titleId = targetTitleId,
        navigationIcon = {
            IconButton(onClick = remember { { router.back() } }) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = null,
                )
            }
        },
        titleContent = { titleId ->
            when (titleId) {
                MainTitleId -> AppTitleImage()
                SecondTittleId -> Text(text = stringResource(id = R.string.storages))
            }
        },
    )


    FabSimpleInContainer(
        onClick = remember { { router.navigate(EditStorageDestination()) } },
        content = { Icon(Icons.Default.Add, contentDescription = "Add") }
    )


}

