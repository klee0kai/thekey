@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.klee0kai.thekey.app.ui.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxDefaults
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.simpleboard.SimpleNavigationBoard
import com.github.klee0kai.thekey.core.ui.devkit.EmptyScreen
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.navigation.model.Destination
import com.github.klee0kai.thekey.core.utils.views.collectAsState
import com.github.klee0kai.thekey.core.utils.views.horizontal
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef
import com.github.klee0kai.thekey.core.utils.views.rememberTickerOf
import com.github.klee0kai.thekey.core.utils.views.thenIf
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.NavAction
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.NavTransitionQueueing
import dev.olshevski.navigation.reimagined.NavTransitionScope
import dev.olshevski.navigation.reimagined.NavTransitionSpec

@Composable
fun MainNavContainer() {
    NavBackHandler(LocalRouter.current.navFullController)
    LocalRouter.current.collectBackstackChanges()
    val presenter by rememberOnScreenRef { DI.mainPresenter() }
    val isBlur by presenter!!.isMakeBlur
        .collectAsState(key = Unit, initial = false)
    val blurDp by animateDpAsState(targetValue = if (isBlur) 10.dp else 0.dp, label = "blur")

    ModalNavigationDrawer(
        drawerState = LocalRouter.current.navBoardState,
        drawerContent = {
            AnimatedNavHost(
                controller = LocalRouter.current.navBoardController,
                transitionQueueing = NavTransitionQueueing.QueueAll,
                transitionSpec = customTransitionSpec,
                emptyBackstackPlaceholder = { SimpleNavigationBoard() }
            ) { destination ->
                DI.screenResolver().screenOf(destination = destination)
            }
        }
    ) {
        // screens
        AnimatedNavHost(
            controller = LocalRouter.current.navScreensController,
            transitionQueueing = NavTransitionQueueing.QueueAll,
            transitionSpec = customTransitionSpec,
            emptyBackstackPlaceholder = { EmptyScreen() }
        ) { destination ->
            Box(modifier = Modifier.thenIf(blurDp > 0.dp) { blur(blurDp) }) {
                DI.screenResolver().screenOf(destination = destination)
            }
        }
    }

    // Dialogs
    AnimatedNavHost(
        controller = LocalRouter.current.navDialogsController,
        transitionQueueing = NavTransitionQueueing.QueueAll,
        transitionSpec = customTransitionSpec,
        emptyBackstackPlaceholder = { Box(modifier = Modifier.fillMaxSize()) }
    ) { destination ->
        Box(modifier = Modifier.thenIf(blurDp > 0.dp) { blur(blurDp) }) {
            DI.screenResolver().screenOf(destination = destination)
        }
    }

    // snack
    SnackContainer()
}

@Composable
fun SnackContainer() {
    val snackbarHostState = LocalRouter.current.snackbarHostState
    val density = LocalDensity.current
    val safeContentPaddings = WindowInsets.safeContent.asPaddingValues()
    val positionalThreshold = SwipeToDismissBoxDefaults.positionalThreshold
    val newSnackTicker by rememberTickerOf { snackbarHostState.currentSnackbarData != null }

    val dismissSnackbarState = remember(newSnackTicker) {
        SwipeToDismissBoxState(
            initialValue = SwipeToDismissBoxValue.Settled,
            confirmValueChange = { true },
            density = density,
            positionalThreshold = positionalThreshold,
        )
    }
    val swipeAlpha = if (dismissSnackbarState.targetValue != SwipeToDismissBoxValue.Settled)
        (1f - dismissSnackbarState.progress * 1.1f).coerceIn(0f, 1f) else 1f
    val swiped = swipeAlpha <= 0

    if (swiped) {
        snackbarHostState.currentSnackbarData?.dismiss()
        return
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = safeContentPaddings.calculateBottomPadding())
                .padding(bottom = 92.dp),
        ) { data ->
            SwipeToDismissBox(
                state = dismissSnackbarState,
                backgroundContent = { },
                modifier = Modifier
                    .fillMaxWidth(),
                enableDismissFromStartToEnd = true,
                enableDismissFromEndToStart = true,
                content = {
                    Snackbar(
                        modifier = Modifier
                            .padding(horizontal = safeContentPaddings.horizontal(minValue = 16.dp))
                            .alpha(swipeAlpha),
                        snackbarData = data,
                    )
                }
            )
        }
    }
}


private val customTransitionSpec = object : NavTransitionSpec<Destination> {

    override fun NavTransitionScope.getContentTransform(
        action: NavAction,
        from: Destination,
        to: Destination
    ): ContentTransform {
        return crossfade()
    }

    override fun NavTransitionScope.fromEmptyBackstack(
        action: NavAction,
        to: Destination
    ): ContentTransform {
        return crossfade()
    }

    override fun NavTransitionScope.toEmptyBackstack(
        action: NavAction,
        from: Destination
    ): ContentTransform {
        return crossfade()
    }

    private fun crossfade() = fadeIn(tween()) togetherWith fadeOut(tween())

    private fun none() = EnterTransition.None togetherWith ExitTransition.None
}
