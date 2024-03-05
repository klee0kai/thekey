package com.github.klee0kai.thekey.app.ui.navigation

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.isDebugInspectorInfoEnabled
import com.github.klee0kai.thekey.app.BuildConfig
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.updateConfig
import com.github.klee0kai.thekey.app.ui.designkit.EmptyScreen
import com.github.klee0kai.thekey.app.ui.editstorage.EditStorageScreen
import com.github.klee0kai.thekey.app.ui.login.LoginScreen
import com.github.klee0kai.thekey.app.ui.note.NoteScreen
import com.github.klee0kai.thekey.app.ui.storage.StorageScreen
import com.github.klee0kai.thekey.app.ui.storages.StoragesScreen
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.NavAction
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.NavTransitionQueueing
import dev.olshevski.navigation.reimagined.NavTransitionScope
import dev.olshevski.navigation.reimagined.NavTransitionSpec
import dev.olshevski.navigation.reimagined.navController


val LocalNav = compositionLocalOf<NavController<Destination>> { error("no local provided NavLocal") }

@Composable
fun MainNavContainer() {
    val navController = rememberSaveable {
        DI.navigator(
            runCatching { DI.navigator() }.getOrNull()
                ?: navController(startDestination = LoginDestination)
        )
    }
    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val isEditMode = LocalView.current.isInEditMode || LocalInspectionMode.current || isDebugInspectorInfoEnabled

    LaunchedEffect(Unit) {
        DI.backDispatcher(backPressedDispatcher)

        DI.updateConfig {
            copy(isViewEditMode = isEditMode)
        }
    }

    NavBackHandler(navController)

    navController.cleanNotUselessResultFlows()

    CompositionLocalProvider(LocalNav provides DI.navigator()) {
        AnimatedNavHost(
            controller = navController,
            transitionQueueing = NavTransitionQueueing.QueueAll,
            transitionSpec = customTransitionSpec,
            emptyBackstackPlaceholder = { EmptyScreen() }
        ) { destination ->
            when (destination) {
                is LoginDestination -> LoginScreen()
                is StoragesDestination -> StoragesScreen()
                is EditStorageDestination -> EditStorageScreen(path = destination.path)
                is StorageDestination -> StorageScreen(destination)
                is NoteDestination -> NoteScreen(destination)

                // debug
                is DesignDestination -> if (BuildConfig.DEBUG) EmptyScreen()
            }
        }
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
