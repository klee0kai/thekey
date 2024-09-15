package com.github.klee0kai.thekey.app.ui.simpleboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.hardResetToPreview
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.ui.navigation.model.SettingsDestination
import com.github.klee0kai.thekey.app.ui.simpleboard.components.StorageNavigationMapList
import com.github.klee0kai.thekey.app.ui.simpleboard.presenter.SimpleBoardPresenterDummy
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.ui.devkit.LocalColorScheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.components.settings.Preference
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.views.DebugDarkContentPreview
import com.github.klee0kai.thekey.core.utils.views.currentRef
import com.github.klee0kai.thekey.core.utils.views.rememberClickDebounced
import org.jetbrains.annotations.VisibleForTesting

@Composable
fun SimpleBoard(
    modifier: Modifier = Modifier,
) {
    val colorScheme = LocalColorScheme.current.navigationBoard
    val router by LocalRouter.currentRef
    val scope = rememberCoroutineScope()
    val safeContentPaddings = WindowInsets.safeContent.asPaddingValues()

    Box(modifier = modifier) {
        StorageNavigationMapList(
            modifier = Modifier
                .align(alignment = Alignment.TopCenter)
                .fillMaxSize(),
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
                .align(alignment = Alignment.BottomCenter),
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
    DebugDarkContentPreview {
        SimpleBoard()
    }
}