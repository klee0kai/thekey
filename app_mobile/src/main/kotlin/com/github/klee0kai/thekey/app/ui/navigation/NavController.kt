package com.github.klee0kai.thekey.app.ui.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.login.LoginScreen
import com.github.klee0kai.thekey.app.ui.storages.StoragesScreen
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.NavAction
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.NavTransitionScope
import dev.olshevski.navigation.reimagined.NavTransitionSpec
import dev.olshevski.navigation.reimagined.navController

@Composable
fun MainNavContainer() {
    val navController = rememberSaveable {
        DI.navigator(navController(startDestination = Destination.LoginScreen))
    }
    NavBackHandler(navController)

    AnimatedNavHost(
        controller = navController,
        transitionSpec = customTransitionSpec
    ) { destination ->
        when (destination) {
            is Destination.LoginScreen -> LoginScreen()
            is Destination.StoragesScreen -> StoragesScreen()
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
