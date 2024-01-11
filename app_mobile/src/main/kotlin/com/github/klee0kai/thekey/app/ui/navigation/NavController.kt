package com.github.klee0kai.thekey.app.ui.navigation

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.github.klee0kai.thekey.app.BuildConfig
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.designkit.EmptyScreen
import com.github.klee0kai.thekey.app.ui.editstorage.EditStorageScreen
import com.github.klee0kai.thekey.app.ui.login.LoginScreen
import com.github.klee0kai.thekey.app.ui.note.NoteScreen
import com.github.klee0kai.thekey.app.ui.storage.StorageScreen
import com.github.klee0kai.thekey.app.ui.storages.StoragesScreen
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.NavAction
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.NavTransitionQueueing
import dev.olshevski.navigation.reimagined.NavTransitionScope
import dev.olshevski.navigation.reimagined.NavTransitionSpec
import dev.olshevski.navigation.reimagined.navController

@Composable
fun MainNavContainer() {
    val navController = remember {
        DI.navigator(navController(startDestination = LoginDestination))
    }
    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    LaunchedEffect(Unit) {
        DI.backDispatcher(backPressedDispatcher)
    }

    NavBackHandler(navController)

    navController.cleanNotUselessResultFlows()

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
            is StorageDestination -> StorageScreen(path = destination.path)
            is NoteDestination -> NoteScreen(storagePath = destination.path, notePtr = destination.notePtr)

            // debug
            is DesignDestination -> if (BuildConfig.DEBUG) EmptyScreen()
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
