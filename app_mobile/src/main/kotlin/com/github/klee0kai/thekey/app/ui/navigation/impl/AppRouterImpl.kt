package com.github.klee0kai.thekey.app.ui.navigation.impl

import com.github.klee0kai.thekey.core.ui.navigation.ActivityRouter
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.ui.navigation.ComposeRouter
import com.github.klee0kai.thekey.core.ui.navigation.NavBoardRouter
import com.github.klee0kai.thekey.core.ui.navigation.PermissionsRouter
import com.github.klee0kai.thekey.core.ui.navigation.RouterContext
import com.github.klee0kai.thekey.core.ui.navigation.SnackRouter

open class AppRouterImp(
    private val ctx: RouterContext = RouterContextImpl()
) : AppRouter,
    ComposeRouter by ComposeRouterImpl(ctx),
    SnackRouter by SnackRouterImpl(ctx),
    NavBoardRouter by NavBoardRouterImpl(ctx),
    ActivityRouter by ActivityRouterImpl(ctx),
    PermissionsRouter by PermissionRouterImpl(ctx) {

    override val showInitDynamicFeatureScreen get() = ctx.showInitDynamicFeatureScreen

    override val snackbarHostState get() = ctx.snackbarHostState

    override val navBoardState get() = ctx.navBoardState

    override val navFullController get() = ctx.navFullController
    override val navScreensController get() = ctx.navScreensController
    override val navDialogsController get() = ctx.navDialogsController

    override val activity get() = ctx.activity

    override val backDispatcher get() = ctx.backDispatcher

    override val navChanges get() = ctx.navChanges

    override val scope get() = ctx.scope

    override fun genRequestCode(): Int = ctx.genRequestCode()

}    
        
    


