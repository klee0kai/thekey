package com.github.klee0kai.thekey.app.ui.simpleboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.hardResetToPreview
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.ui.navigation.model.SettingsDestination
import com.github.klee0kai.thekey.app.ui.simpleboard.components.CurrentStorageHeader
import com.github.klee0kai.thekey.app.ui.simpleboard.components.DefaultHeader
import com.github.klee0kai.thekey.app.ui.simpleboard.components.StorageNavigationMapList
import com.github.klee0kai.thekey.app.ui.simpleboard.presenter.SimpleBoardPresenterDummy
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.domain.model.ColoredStorage
import com.github.klee0kai.thekey.core.ui.devkit.LocalColorScheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.components.settings.Preference
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.views.DebugDarkScreenPreview
import com.github.klee0kai.thekey.core.utils.views.collectAsStateCrossFaded
import com.github.klee0kai.thekey.core.utils.views.currentRef
import com.github.klee0kai.thekey.core.utils.views.horizontal
import com.github.klee0kai.thekey.core.utils.views.rememberClickDebounced
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef
import org.jetbrains.annotations.VisibleForTesting

@Composable
fun SimpleNavigationBoard(
    modifier: Modifier = Modifier,
) {
    val theme = LocalTheme.current
    val colorScheme = LocalColorScheme.current.navigationBoard
    val router by LocalRouter.currentRef
    val scope = rememberCoroutineScope()
    val safeContentPaddings = WindowInsets.safeContent.asPaddingValues()
    val safeDrawingPaddings = WindowInsets.safeDrawing.asPaddingValues()
    val presenter by rememberOnScreenRef { DI.simpleBoardPresenter() }
    val currentStorage by presenter!!.currentStorage.collectAsStateCrossFaded(
        key = Unit,
        initial = null
    )

    ConstraintLayout(
        modifier = modifier
            .width(300.dp)
            .fillMaxHeight()
            .background(colorScheme.bodySurfaceColor),
    ) {
        val (
            headerLayout,
            storagesListLayout,
            settingButtonField,
        ) = createRefs()

        if (currentStorage.current != null) {
            CurrentStorageHeader(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 150.dp)
                    .background(colorScheme.headerSurfaceColor)
                    .padding(top = safeDrawingPaddings.calculateTopPadding())
                    .padding(start = safeDrawingPaddings.horizontal(minValue = 16.dp))
                    .constrainAs(headerLayout) { },
                storage = currentStorage.current ?: ColoredStorage(),
            )
        } else {
            DefaultHeader(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 150.dp)
                    .background(colorScheme.headerSurfaceColor)
                    .padding(top = safeDrawingPaddings.calculateTopPadding())
                    .padding(start = safeDrawingPaddings.horizontal(minValue = 16.dp))
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
            footer = {
                Spacer(
                    modifier = Modifier.height(safeContentPaddings.calculateBottomPadding() + 56.dp)
                )
            },
        )


        Preference(
            text = stringResource(id = R.string.settings),
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        0f to Color.Transparent,
                        0.5f to colorScheme.bodySurfaceColor,
                        1f to colorScheme.bodySurfaceColor,
                        start = Offset.Zero,
                        end = Offset(0f, Float.POSITIVE_INFINITY)
                    )
                )
                .padding(
                    top = 16.dp,
                    bottom = safeContentPaddings.calculateBottomPadding(),
                )
                .fillMaxWidth()
                .constrainAs(settingButtonField) {
                    linkTo(
                        top = headerLayout.bottom,
                        bottom = parent.bottom,
                        start = parent.start,
                        end = parent.end,
                        verticalBias = 1f,
                    )
                },
            onClick = rememberClickDebounced {
                router?.navigate(SettingsDestination)
                router?.hideNavigationBoard()
            }
        )
    }
}


@VisibleForTesting
@OptIn(DebugOnly::class)
@Preview(device = Devices.PHONE)
@Composable
fun SimpleBoardPreview() {
    DI.hardResetToPreview()
    DI.initPresenterModule(object : PresentersModule {
        override fun simpleBoardPresenter() = SimpleBoardPresenterDummy(
            hasCurrentStorage = true,
            opennedCount = 10,
            favoriteCount = 10,
        )
    })
    DebugDarkScreenPreview {
        SimpleNavigationBoard()
    }
}


@VisibleForTesting
@OptIn(DebugOnly::class)
@Preview(device = Devices.PHONE)
@Composable
fun SimpleBoardNoCurrentPreview() {
    DI.hardResetToPreview()
    DI.initPresenterModule(object : PresentersModule {
        override fun simpleBoardPresenter() = SimpleBoardPresenterDummy(
        )
    })
    DebugDarkScreenPreview {
        SimpleNavigationBoard()
    }
}

@VisibleForTesting
@OptIn(DebugOnly::class)
@Preview(device = Devices.PHONE)
@Composable
fun SimpleBoardEmptyPreview() {
    DI.hardResetToPreview()
    DI.initPresenterModule(object : PresentersModule {
        override fun simpleBoardPresenter() = SimpleBoardPresenterDummy()
    })
    DebugDarkScreenPreview {
        SimpleNavigationBoard()
    }
}


@VisibleForTesting
@OptIn(DebugOnly::class)
@Preview(device = Devices.TABLET)
@Composable
fun SimpleBoardTabletPreview() {
    DI.hardResetToPreview()
    DI.initPresenterModule(object : PresentersModule {
        override fun simpleBoardPresenter() = SimpleBoardPresenterDummy()
    })
    DebugDarkScreenPreview {
        SimpleNavigationBoard()
    }
}