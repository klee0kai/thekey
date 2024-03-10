package com.github.klee0kai.thekey.app.ui.navigation.impl

import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedDispatcher
import androidx.compose.material3.SnackbarHostState
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.navigation.RouterContext
import com.github.klee0kai.thekey.app.ui.navigation.model.Destination
import com.github.klee0kai.thekey.app.ui.navigation.model.LoginDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.NavigateBackstackChange
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.navController
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlin.random.Random

class RouterContextImpl : RouterContext {

    override val activity: ComponentActivity? get() = DI.activity()
    override val backDispatcher: OnBackPressedDispatcher? get() = DI.activity()?.onBackPressedDispatcher

    override val snackbarHostState: SnackbarHostState = SnackbarHostState()
    override val navFullController: NavController<Destination> = navController(startDestination)
    override val navScreensController: NavController<Destination> = navController(startDestination)
    override val navDialogsController: NavController<Destination> = navController(emptyList())

    override val navChanges = MutableSharedFlow<NavigateBackstackChange>(replay = 1)
    override val scope = DI.mainThreadScope()

    private var _reqCodeCounter = Random.nextInt(1000) + 1
    override fun genRequestCode(): Int = _reqCodeCounter++

    companion object {
        private val startDestination = LoginDestination
    }

}