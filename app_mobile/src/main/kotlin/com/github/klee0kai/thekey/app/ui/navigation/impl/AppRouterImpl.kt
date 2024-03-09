package com.github.klee0kai.thekey.app.ui.navigation.impl

import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedDispatcher
import androidx.compose.material3.SnackbarHostState
import com.github.klee0kai.thekey.app.ui.navigation.ActivityRouter
import com.github.klee0kai.thekey.app.ui.navigation.AppRouter
import com.github.klee0kai.thekey.app.ui.navigation.ComposeRouter
import com.github.klee0kai.thekey.app.ui.navigation.PermissionsRouter
import com.github.klee0kai.thekey.app.ui.navigation.RouterContext
import com.github.klee0kai.thekey.app.ui.navigation.SnackRouter
import com.github.klee0kai.thekey.app.ui.navigation.model.Destination
import com.github.klee0kai.thekey.app.ui.navigation.model.NavigateBackstackChange
import dev.olshevski.navigation.reimagined.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow

open class AppRouterImp(
    private val ctx: RouterContext = RouterContextImpl()
) : AppRouter,
    ComposeRouter by ComposeRouterImpl(ctx),
    SnackRouter by SnackRouterImpl(ctx),
    ActivityRouter by ActivityRouterImpl(ctx),
    PermissionsRouter by PermissionRouterImpl(ctx) {

    override val snackbarHostState: SnackbarHostState
        get() = ctx.snackbarHostState

    override val navFullController: NavController<Destination>
        get() = ctx.navFullController
    override val navScreensController: NavController<Destination>
        get() = ctx.navScreensController
    override val navDialogsController: NavController<Destination>
        get() = ctx.navDialogsController

    override val activity: ComponentActivity?
        get() = ctx.activity

    override val backDispatcher: OnBackPressedDispatcher?
        get() = ctx.backDispatcher

    override val navChanges: MutableSharedFlow<NavigateBackstackChange>
        get() = ctx.navChanges

    override val scope: CoroutineScope
        get() = ctx.scope

}    
        
    


