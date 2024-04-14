package com.github.klee0kai.thekey.app.ui.navigation.impl

import com.github.klee0kai.thekey.app.ui.navigation.NavBoardRouter
import com.github.klee0kai.thekey.app.ui.navigation.RouterContext

class NavBoardRouterImpl(
    val context: RouterContext
) : NavBoardRouter, RouterContext by context {

    override fun isNavigationBoardIsOpen() = navBoardState.isOpen

    override suspend fun showNavigationBoard() {
        navBoardState.open()
    }

    override suspend fun hideNavigationBoard() {
        navBoardState.close()
    }
}