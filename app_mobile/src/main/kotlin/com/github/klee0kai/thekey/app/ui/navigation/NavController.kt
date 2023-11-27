package com.github.klee0kai.thekey.app.ui.navigation

import androidx.compose.runtime.Composable
import com.github.klee0kai.thekey.app.ui.main.MainScreen
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.NavHost
import dev.olshevski.navigation.reimagined.rememberNavController

@Composable
fun MainNavContainer() {
    val navController = rememberNavController<Destination>(
        startDestination = Destination.MainScreen
    )
    NavBackHandler(navController)
    NavHost(navController) { destination ->
        when (destination) {
            is Destination.MainScreen -> MainScreen()
        }
    }

}