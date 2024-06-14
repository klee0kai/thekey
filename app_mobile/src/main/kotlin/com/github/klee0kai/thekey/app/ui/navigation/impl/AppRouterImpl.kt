package com.github.klee0kai.thekey.app.ui.navigation.impl

import android.content.Intent
import com.github.klee0kai.thekey.core.di.identifiers.ActivityIdentifier
import com.github.klee0kai.thekey.core.ui.navigation.ActivityRouter
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.ui.navigation.ComposeRouter
import com.github.klee0kai.thekey.core.ui.navigation.DeeplinkRouter
import com.github.klee0kai.thekey.core.ui.navigation.NavBoardRouter
import com.github.klee0kai.thekey.core.ui.navigation.PermissionsRouter
import com.github.klee0kai.thekey.core.ui.navigation.RouterContext
import com.github.klee0kai.thekey.core.ui.navigation.SnackRouter
import com.github.klee0kai.thekey.core.ui.navigation.deeplink.DeeplinkRoute
import com.github.klee0kai.thekey.core.ui.navigation.model.Destination

open class AppRouterImp(
    override val activityIdentifier: ActivityIdentifier?,
    private val ctx: RouterContext = RouterContextImpl(activityIdentifier)
) : AppRouter,
    ComposeRouter by ComposeRouterImpl(ctx),
    DeeplinkRouter,
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

    private var deeplinkRoute = DeeplinkRoute()

    override fun genRequestCode(): Int = ctx.genRequestCode()

    override suspend fun handleDeeplink(intent: Intent) =
        deeplinkRoute.processDeeplink(intent, this)


    override fun configDeeplinks(block: DeeplinkRoute.() -> Unit) {
        block.invoke(deeplinkRoute)
    }

    override fun initDestination(dest: Destination) {
        ctx.initDestination(dest)
    }

}
        
    


