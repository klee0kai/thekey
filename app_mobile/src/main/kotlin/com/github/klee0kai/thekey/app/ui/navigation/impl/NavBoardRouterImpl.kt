package com.github.klee0kai.thekey.app.ui.navigation.impl

import com.github.klee0kai.thekey.core.ui.navigation.NavBoardRouter
import com.github.klee0kai.thekey.core.ui.navigation.RouterContext
import com.github.klee0kai.thekey.core.utils.common.launch
import kotlinx.coroutines.flow.MutableStateFlow

class NavBoardRouterImpl(
    val context: RouterContext
) : NavBoardRouter, RouterContext by context {

    override val isNavBoardOpen = MutableStateFlow(false)

    override fun showNavigationBoard() {
        scope.launch {
            navBoardState.open()
            isNavBoardOpen.value = navBoardState.isOpen
        }
    }

    override fun hideNavigationBoard() {
        scope.launch {
            navBoardState.close()
            isNavBoardOpen.value = navBoardState.isOpen
        }
    }
}