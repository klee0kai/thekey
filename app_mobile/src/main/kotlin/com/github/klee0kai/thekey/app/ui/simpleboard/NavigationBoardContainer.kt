package com.github.klee0kai.thekey.app.ui.simpleboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.hardResetToPreview
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.ui.navigation.customTransitionSpec
import com.github.klee0kai.thekey.app.ui.simpleboard.components.CurrentStorageHeader
import com.github.klee0kai.thekey.app.ui.simpleboard.components.DefaultHeader
import com.github.klee0kai.thekey.app.ui.simpleboard.presenter.SimpleBoardPresenterDummy
import com.github.klee0kai.thekey.core.domain.model.ColoredStorage
import com.github.klee0kai.thekey.core.ui.devkit.LocalColorScheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.views.DebugDarkScreenPreview
import com.github.klee0kai.thekey.core.utils.views.collectAsStateFaded
import com.github.klee0kai.thekey.core.utils.views.currentRef
import com.github.klee0kai.thekey.core.utils.views.horizontal
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.NavTransitionQueueing
import org.jetbrains.annotations.VisibleForTesting

@Composable
fun NavigationBoardContainer(
    modifier: Modifier = Modifier,
) {
    val theme = LocalTheme.current
    val colorScheme = LocalColorScheme.current.navigationBoard
    val router by LocalRouter.currentRef
    val scope = rememberCoroutineScope()
    val safeDrawingPaddings = WindowInsets.safeDrawing.asPaddingValues()
    val presenter by rememberOnScreenRef { DI.simpleBoardPresenter() }
    val currentStorage by presenter!!.currentStorage.collectAsStateFaded(
        key = Unit,
        initial = null
    )

    ConstraintLayout(
        modifier = modifier
            .width(300.dp)
            .fillMaxHeight()
            .background(colorScheme.bodyBackgroundColor),
    ) {
        val (
            headerField,
            navBoardContentField,
        ) = createRefs()

        if (currentStorage.current != null) {
            CurrentStorageHeader(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 150.dp)
                    .background(colorScheme.headerBackgroundColor)
                    .padding(top = safeDrawingPaddings.calculateTopPadding())
                    .padding(start = safeDrawingPaddings.horizontal(minValue = 16.dp))
                    .constrainAs(headerField) { },
                storage = currentStorage.current ?: ColoredStorage(),
            )
        } else {
            DefaultHeader(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 150.dp)
                    .background(colorScheme.headerBackgroundColor)
                    .padding(top = safeDrawingPaddings.calculateTopPadding())
                    .padding(start = safeDrawingPaddings.horizontal(minValue = 16.dp))
                    .constrainAs(headerField) { },
            )
        }


        AnimatedNavHost(
            modifier = Modifier.constrainAs(navBoardContentField) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
                linkTo(
                    top = headerField.bottom,
                    bottom = parent.bottom,
                    start = parent.start,
                    end = parent.end,
                )
            },
            controller = LocalRouter.current.navBoardController,
            transitionQueueing = NavTransitionQueueing.QueueAll,
            transitionSpec = customTransitionSpec,
            emptyBackstackPlaceholder = { SimpleBoard() }
        ) { destination ->
            DI.screenResolver().screenOf(destination = destination)
        }

    }
}


@VisibleForTesting
@OptIn(DebugOnly::class)
@Preview(device = Devices.PHONE)
@Composable
fun NavigationBoardContainerPreview() {
    DI.hardResetToPreview()
    DI.initPresenterModule(object : PresentersModule {
        override fun simpleBoardPresenter() = SimpleBoardPresenterDummy(
            hasCurrentStorage = true,
            opennedCount = 10,
            favoriteCount = 10,
        )
    })
    DebugDarkScreenPreview {
        NavigationBoardContainer()
    }
}


@VisibleForTesting
@OptIn(DebugOnly::class)
@Preview(device = Devices.PHONE)
@Composable
fun NavigationBoardNoCurrentPreview() {
    DI.hardResetToPreview()
    DI.initPresenterModule(object : PresentersModule {
        override fun simpleBoardPresenter() = SimpleBoardPresenterDummy(
        )
    })
    DebugDarkScreenPreview {
        NavigationBoardContainer()
    }
}

@VisibleForTesting
@OptIn(DebugOnly::class)
@Preview(device = Devices.PHONE)
@Composable
fun NavigationBoardEmptyPreview() {
    DI.hardResetToPreview()
    DI.initPresenterModule(object : PresentersModule {
        override fun simpleBoardPresenter() = SimpleBoardPresenterDummy()
    })
    DebugDarkScreenPreview {
        NavigationBoardContainer()
    }
}


@VisibleForTesting
@OptIn(DebugOnly::class)
@Preview(device = Devices.TABLET)
@Composable
fun NavigationBoardTabletPreview() {
    DI.hardResetToPreview()
    DI.initPresenterModule(object : PresentersModule {
        override fun simpleBoardPresenter() = SimpleBoardPresenterDummy()
    })
    DebugDarkScreenPreview {
        NavigationBoardContainer()
    }
}