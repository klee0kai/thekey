package com.github.klee0kai.thekey.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.login.LoginScreen
import com.github.klee0kai.thekey.app.ui.storages.StoragesScreen
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.NavHost
import dev.olshevski.navigation.reimagined.navController

@Composable
fun MainNavContainer() {
    val navController = rememberSaveable {
        DI.navigator(navController(startDestination = Destination.LoginScreen))
    }
    NavBackHandler(navController)
    NavHost(navController) { destination ->
        when (destination) {
            is Destination.LoginScreen -> LoginScreen()
            is Destination.StoragesScreen -> StoragesScreen()
        }
    }
}