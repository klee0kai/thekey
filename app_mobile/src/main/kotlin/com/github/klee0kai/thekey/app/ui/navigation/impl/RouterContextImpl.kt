package com.github.klee0kai.thekey.app.ui.navigation.impl

import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedDispatcher
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.SnackbarHostState
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.navigation.model.MainDestinations
import com.github.klee0kai.thekey.core.di.identifiers.ActivityIdentifier
import com.github.klee0kai.thekey.core.ui.navigation.RouterContext
import com.github.klee0kai.thekey.core.ui.navigation.model.Destination
import com.github.klee0kai.thekey.core.ui.navigation.model.NavigateBackstackChange
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.navController
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.random.Random

class RouterContextImpl(
    override val activityIdentifier: ActivityIdentifier?
) : RouterContext {

    private var initDestination: Destination = MainDestinations.InitDest

    override val showInitDynamicFeatureScreen = MutableStateFlow(false)

    override val activity: ComponentActivity? get() = DI.activity()
    override val backDispatcher: OnBackPressedDispatcher? get() = DI.activity()?.onBackPressedDispatcher

    override val snackbarHostState: SnackbarHostState = SnackbarHostState()
    override val navBoardState: DrawerState = DrawerState(DrawerValue.Closed)
    override val navFullController: NavController<Destination> by lazy {
        navController(initDestination)
    }
    override val navScreensController: NavController<Destination> by lazy {
        navController(initDestination)
    }
    override val navDialogsController: NavController<Destination> by lazy { navController(emptyList()) }

    override val navBoardController: NavController<Destination> by lazy { navController(emptyList()) }

    override val navChanges = MutableSharedFlow<NavigateBackstackChange>(replay = 1)
    override val scope = DI.androidUiScope()

    private var _reqCodeCounter = Random.nextInt(1000) + 1
    override fun genRequestCode(): Int = _reqCodeCounter++

    override fun initDestination(dest: Destination) {
        initDestination = dest
    }

}