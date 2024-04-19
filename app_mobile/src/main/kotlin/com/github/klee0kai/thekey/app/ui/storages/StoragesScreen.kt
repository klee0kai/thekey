package com.github.klee0kai.thekey.app.ui.storages

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.designkit.LocalRouter
import com.github.klee0kai.thekey.app.ui.designkit.components.FabSimpleInContainer
import com.github.klee0kai.thekey.app.ui.designkit.components.appbar.AppBarConst
import com.github.klee0kai.thekey.app.ui.designkit.components.appbar.AppBarStates
import com.github.klee0kai.thekey.app.ui.designkit.components.appbar.AppTitleImage
import com.github.klee0kai.thekey.app.ui.designkit.components.appbar.rememberMainTitleVisibleFlow
import com.github.klee0kai.thekey.app.ui.designkit.components.bottomsheet.SimpleBottomSheetScaffold
import com.github.klee0kai.thekey.app.ui.designkit.components.bottomsheet.rememberSimpleBottomSheetScaffoldState
import com.github.klee0kai.thekey.app.ui.designkit.components.bottomsheet.topContentAlphaFromDrag
import com.github.klee0kai.thekey.app.ui.designkit.components.bottomsheet.topContentOffsetFromDrag
import com.github.klee0kai.thekey.app.ui.navigation.model.EditStorageDestination
import com.github.klee0kai.thekey.app.ui.storages.components.GroupsSelectContent
import com.github.klee0kai.thekey.app.ui.storages.components.StoragesListContent
import com.github.klee0kai.thekey.app.utils.views.rememberDerivedStateOf

private const val MainTitleId = 0
private const val SecondTittleId = 1

@Preview
@Composable
fun StoragesScreen() {
    val presenter = remember { DI.storagesPresenter() }
    val router = LocalRouter.current
    var dragProgress = remember { mutableFloatStateOf(0f) }
    val scaffoldState = rememberSimpleBottomSheetScaffoldState(
        topContentSize = 190.dp,
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
    val showStoragesTitle by rememberDerivedStateOf { dragProgress.value > 0.1f }

    SideEffect {
        presenter.startup()
    }

    SimpleBottomSheetScaffold(
        simpleBottomSheetScaffoldState = scaffoldState,
        onDrag = { dragProgress.floatValue = it },
        topContent = {
            GroupsSelectContent(
                modifier = Modifier
                    .alpha(dragProgress.floatValue.topContentAlphaFromDrag())
                    .offset(y = dragProgress.floatValue.topContentOffsetFromDrag()),
            )
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

