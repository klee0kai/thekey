package com.github.klee0kai.thekey.app.ui.navigationboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.hardReset
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.domain.model.ColoredStorage
import com.github.klee0kai.thekey.app.ui.designkit.AppTheme
import com.github.klee0kai.thekey.app.ui.designkit.LocalColorScheme
import com.github.klee0kai.thekey.app.ui.designkit.LocalRouter
import com.github.klee0kai.thekey.app.ui.navigation.model.SettingsDestination
import com.github.klee0kai.thekey.app.ui.navigationboard.components.CurrentStorageHeader
import com.github.klee0kai.thekey.app.ui.navigationboard.components.DefaultHeader
import com.github.klee0kai.thekey.app.ui.navigationboard.components.StorageNavigationMapList
import com.github.klee0kai.thekey.app.ui.navigationboard.presenter.NavigationBoardPresenterDummy
import com.github.klee0kai.thekey.app.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.app.utils.views.collectAsStateCrossFaded

@Composable
fun StorageNavigationBoard(modifier: Modifier = Modifier) {
    val colorScheme = LocalColorScheme.current.navigationBoard
    val router = LocalRouter.current
    val presenter = remember { DI.navigationBoardPresenter() }
    val currentStorage by presenter.currentStorage.collectAsStateCrossFaded(key = Unit, initial = null)

    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.bodyContentColor),
    ) {
        val (
            headerLayout,
            storagesListLayout,
            settingsButtonField,
        ) = createRefs()

        if (currentStorage.current != null) {
            CurrentStorageHeader(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 150.dp)
                    .background(colorScheme.headerContainerColor)
                    .constrainAs(headerLayout) { },
                storage = currentStorage.current ?: ColoredStorage(),
            )
        } else {
            DefaultHeader(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 150.dp)
                    .background(colorScheme.headerContainerColor)
                    .constrainAs(headerLayout) { },
            )
        }

        StorageNavigationMapList(
            modifier = Modifier
                .constrainAs(storagesListLayout) {
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                    linkTo(
                        top = headerLayout.bottom,
                        bottom = parent.bottom,
                        start = parent.start,
                        end = parent.end,
                    )
                },
        )

        TextButton(
            modifier = Modifier
                .constrainAs(settingsButtonField) {
                    width = Dimension.fillToConstraints
                    linkTo(
                        top = headerLayout.bottom,
                        bottom = parent.bottom,
                        start = parent.start,
                        end = parent.end,
                        verticalBias = 1f,
                    )
                },
            onClick = { router.navigate(SettingsDestination) },
        ) {
            Text(text = stringResource(id = R.string.settings))
        }
    }
}


@OptIn(DebugOnly::class)
@Preview(showSystemUi = true, device = Devices.PIXEL_6)
@Composable
private fun StorageNavigationBoardPreview() = AppTheme {
    DI.hardReset()
    DI.initPresenterModule(object : PresentersModule {
        override fun navigationBoardPresenter() = NavigationBoardPresenterDummy(
            hasCurrentStorage = true,
            hasFavorites = true,
        )
    })
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.7f)
    ) {
        StorageNavigationBoard()
    }
}

@OptIn(DebugOnly::class)
@Preview(showSystemUi = true, device = Devices.PIXEL_6)
@Composable
private fun StorageNavigationBoardNoCurrentPreview() = AppTheme {
    DI.hardReset()
    DI.initPresenterModule(object : PresentersModule {
        override fun navigationBoardPresenter() = NavigationBoardPresenterDummy(
            hasFavorites = true,
        )
    })
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.7f)
    ) {
        StorageNavigationBoard()
    }
}


@OptIn(DebugOnly::class)
@Preview(showSystemUi = true, device = Devices.PIXEL_6)
@Composable
private fun StorageNavigationBoardEmptyPreview() = AppTheme {
    DI.hardReset()
    DI.initPresenterModule(object : PresentersModule {
        override fun navigationBoardPresenter() = NavigationBoardPresenterDummy()
    })
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.7f)
    ) {
        StorageNavigationBoard()
    }
}