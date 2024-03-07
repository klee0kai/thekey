package com.github.klee0kai.thekey.app.ui.navigation.impl

import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedDispatcher
import androidx.compose.material3.SnackbarHostState
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.navigation.RouterContext
import com.github.klee0kai.thekey.app.ui.navigation.model.Destination
import com.github.klee0kai.thekey.app.ui.navigation.model.LoginDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.NavigateBackstackChange
import com.github.klee0kai.thekey.app.utils.common.WeakDelegate
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.navController
import kotlinx.coroutines.flow.MutableSharedFlow

class RouterContextImpl : RouterContext {

    override var activity: ComponentActivity? by WeakDelegate()
    override var backDispatcher: OnBackPressedDispatcher? by WeakDelegate()

    override val snackbarHostState: SnackbarHostState = SnackbarHostState()
    override val composeController: NavController<Destination> = navController(startDestination = LoginDestination)

    override val navChanges = MutableSharedFlow<NavigateBackstackChange>(replay = 1)
    override val scope = DI.mainThreadScope()

}