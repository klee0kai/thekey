package com.github.klee0kai.thekey.app.ui.navigationboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.hardResetToPreview
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.domain.model.ColoredStorage
import com.github.klee0kai.thekey.app.ui.navigation.model.AboutDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.SettingsDestination
import com.github.klee0kai.thekey.app.ui.navigationboard.components.CurrentStorageHeader
import com.github.klee0kai.thekey.app.ui.navigationboard.components.DefaultHeader
import com.github.klee0kai.thekey.app.ui.navigationboard.components.StorageNavigationMapList
import com.github.klee0kai.thekey.app.ui.navigationboard.presenter.NavigationBoardPresenterDummy
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalColorScheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.views.collectAsStateCrossFaded
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef
import com.github.klee0kai.thekey.core.utils.views.truncate
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import kotlinx.coroutines.launch
import org.jetbrains.annotations.VisibleForTesting

@Composable
fun StorageNavigationBoard(modifier: Modifier = Modifier) {
    val colorScheme = LocalColorScheme.current.navigationBoard
    val router = LocalRouter.current
    val scope = rememberCoroutineScope()
    val presenter by rememberOnScreenRef { DI.navigationBoardPresenter() }
    val currentStorage by presenter!!.currentStorage.collectAsStateCrossFaded(key = Unit, initial = null)

    ConstraintLayout(
        modifier = modifier
            .width(300.dp)
            .fillMaxHeight()
            .background(colorScheme.bodyContentColor),
    ) {
        val (
            headerLayout,
            storagesListLayout,
            settingsButtonField,
            aboutButtonField,
        ) = createRefs()

        if (currentStorage.current != null) {
            CurrentStorageHeader(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 150.dp)
                    .background(colorScheme.headerContainerColor)
                    .windowInsetsPadding(WindowInsets.safeDrawing.truncate(bottom = true))
                    .constrainAs(headerLayout) { },
                storage = currentStorage.current ?: ColoredStorage(),
            )
        } else {
            DefaultHeader(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 150.dp)
                    .background(colorScheme.headerContainerColor)
                    .windowInsetsPadding(WindowInsets.safeDrawing.truncate(bottom = true))
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


        IconButton(
            modifier = Modifier
                .defaultMinSize(64.dp, 64.dp)
                .windowInsetsPadding(WindowInsets.safeContent.truncate(top = true))
                .constrainAs(aboutButtonField) {
                    horizontalChainWeight = 1f

                    linkTo(
                        top = headerLayout.bottom,
                        bottom = parent.bottom,
                        start = parent.start,
                        end = settingsButtonField.start,
                        verticalBias = 1f,
                    )
                },
            onClick = {
                scope.launch {
                    router.navigate(AboutDestination)
                    router.hideNavigationBoard()
                }
            },
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = stringResource(id = R.string.about)
            )
        }


        IconButton(
            modifier = Modifier
                .defaultMinSize(64.dp, 64.dp)
                .windowInsetsPadding(WindowInsets.safeContent.truncate(top = true))
                .constrainAs(settingsButtonField) {
                    horizontalChainWeight = 1f
                    linkTo(
                        top = headerLayout.bottom,
                        bottom = parent.bottom,
                        start = aboutButtonField.end,
                        end = parent.end,
                        verticalBias = 1f,
                    )
                },
            onClick = {
                scope.launch {
                    router.navigate(SettingsDestination)
                    router.hideNavigationBoard()
                }
            }
        ) {
            Icon(
                Icons.Default.Settings,
                contentDescription = stringResource(id = R.string.about)
            )
        }

    }
}


@VisibleForTesting
@OptIn(DebugOnly::class)
@Preview(device = Devices.PHONE)
@Composable
fun StorageNavigationBoardPreview() = EdgeToEdgeTemplate {
    AppTheme {
        DI.hardResetToPreview()
        DI.initPresenterModule(object : PresentersModule {
            override fun navigationBoardPresenter() = NavigationBoardPresenterDummy(
                hasCurrentStorage = true,
                hasFavorites = true,
            )
        })
        StorageNavigationBoard()
    }
}


@VisibleForTesting
@OptIn(DebugOnly::class)
@Preview(device = Devices.PHONE)
@Composable
fun StorageNavigationBoardNoCurrentPreview() = EdgeToEdgeTemplate {
    AppTheme {
        DI.hardResetToPreview()
        DI.initPresenterModule(object : PresentersModule {
            override fun navigationBoardPresenter() = NavigationBoardPresenterDummy(
                hasFavorites = true,
            )
        })

        StorageNavigationBoard()
    }
}

@VisibleForTesting
@OptIn(DebugOnly::class)
@Preview(device = Devices.PHONE)
@Composable
fun StorageNavigationBoardEmptyPreview() = EdgeToEdgeTemplate {
    AppTheme {
        DI.hardResetToPreview()
        DI.initPresenterModule(object : PresentersModule {
            override fun navigationBoardPresenter() = NavigationBoardPresenterDummy()
        })
        StorageNavigationBoard()
    }
}


@VisibleForTesting
@OptIn(DebugOnly::class)
@Preview(device = Devices.TABLET)
@Composable
fun StorageNavigationBoardTabletPreview() = EdgeToEdgeTemplate {
    AppTheme {
        DI.hardResetToPreview()
        DI.initPresenterModule(object : PresentersModule {
            override fun navigationBoardPresenter() = NavigationBoardPresenterDummy()
        })
        StorageNavigationBoard()
    }
}