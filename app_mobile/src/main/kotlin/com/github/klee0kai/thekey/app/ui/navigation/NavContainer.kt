@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.klee0kai.thekey.app.ui.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxDefaults
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.isDebugInspectorInfoEnabled
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.app.BuildConfig
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.updateConfig
import com.github.klee0kai.thekey.app.ui.designkit.AppTheme
import com.github.klee0kai.thekey.app.ui.designkit.EmptyScreen
import com.github.klee0kai.thekey.app.ui.designkit.dialogs.AlertDialogScreen
import com.github.klee0kai.thekey.app.ui.editstorage.EditStorageScreen
import com.github.klee0kai.thekey.app.ui.genhist.GenHistScreen
import com.github.klee0kai.thekey.app.ui.login.LoginScreen
import com.github.klee0kai.thekey.app.ui.navigation.model.AlertDialogDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.DesignDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.Destination
import com.github.klee0kai.thekey.app.ui.navigation.model.EditNoteDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.EditNoteGroupDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.EditStorageDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.GenHistDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.LoginDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.StorageDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.StoragesDestination
import com.github.klee0kai.thekey.app.ui.note.EditNoteScreen
import com.github.klee0kai.thekey.app.ui.notegroup.EditNoteGroupScreen
import com.github.klee0kai.thekey.app.ui.storage.StorageScreen
import com.github.klee0kai.thekey.app.ui.storages.StoragesScreen
import com.github.klee0kai.thekey.app.utils.views.rememberTickerOf
import com.valentinilk.shimmer.defaultShimmerTheme
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.NavAction
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.NavTransitionQueueing
import dev.olshevski.navigation.reimagined.NavTransitionScope
import dev.olshevski.navigation.reimagined.NavTransitionSpec


val LocalRouter = compositionLocalOf { DI.router() }
val LocalShimmerTheme = compositionLocalOf { defaultShimmerTheme.copy() }
val LocalColorScheme = compositionLocalOf { DI.theme().colorScheme() }

@Composable
fun MainNavContainer() {
    val router = remember { DI.router() }
    val isEditMode = LocalView.current.isInEditMode || LocalInspectionMode.current || isDebugInspectorInfoEnabled

    LaunchedEffect(Unit) {
        DI.updateConfig {
            copy(isViewEditMode = isEditMode)
        }
    }

    NavBackHandler(router.navFullController)

    router.collectBackstackChanges()

    CompositionLocalProvider(
        LocalRouter provides DI.router(),
        LocalShimmerTheme provides defaultShimmerTheme.copy(),
        LocalColorScheme provides DI.theme().colorScheme(),
    ) {
        AppTheme {
            // screens
            AnimatedNavHost(
                controller = router.navScreensController,
                transitionQueueing = NavTransitionQueueing.QueueAll,
                transitionSpec = customTransitionSpec,
                emptyBackstackPlaceholder = { EmptyScreen() }
            ) { destination ->
                screenOf(destination = destination)
            }

            // Dialogs
            AnimatedNavHost(
                controller = router.navDialogsController,
                transitionQueueing = NavTransitionQueueing.QueueAll,
                transitionSpec = customTransitionSpec,
            ) { destination ->
                screenOf(destination = destination)
            }

            // snack
            SnackContainer()
        }
    }
}

@Preview
@Composable
fun SnackContainer() {
    val snackbarHostState = LocalRouter.current.snackbarHostState
    val density = LocalDensity.current
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

    Box(modifier = Modifier.fillMaxSize()) {
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 46.dp)
                .alpha(swipeAlpha)
                .align(Alignment.BottomCenter)
        ) { data ->
            SwipeToDismissBox(
                state = dismissSnackbarState,
                backgroundContent = { },
                modifier = Modifier
                    .fillMaxWidth(),
                enableDismissFromStartToEnd = true,
                enableDismissFromEndToStart = true,
                content = { Snackbar(snackbarData = data) }
            )
        }
    }
}

@Composable
@NonRestartableComposable
private fun screenOf(destination: Destination) {
    when (destination) {
        is LoginDestination -> LoginScreen()
        is StoragesDestination -> StoragesScreen()
        is EditStorageDestination -> EditStorageScreen(path = destination.path)
        is StorageDestination -> StorageScreen(destination)
        is GenHistDestination -> GenHistScreen(destination)
        is EditNoteDestination -> EditNoteScreen(destination)
        is EditNoteGroupDestination -> EditNoteGroupScreen(destination)

        is AlertDialogDestination -> AlertDialogScreen(destination)

        // debug
        is DesignDestination -> if (BuildConfig.DEBUG) EmptyScreen()
    }
}


private val customTransitionSpec = object : NavTransitionSpec<Destination> {

    override fun NavTransitionScope.getContentTransform(
        action: NavAction,
        from: Destination,
        to: Destination
    ): ContentTransform {
        return when {
            else -> crossfade()
        }
    }

    private fun crossfade() = fadeIn(tween()) togetherWith fadeOut(tween())
}
