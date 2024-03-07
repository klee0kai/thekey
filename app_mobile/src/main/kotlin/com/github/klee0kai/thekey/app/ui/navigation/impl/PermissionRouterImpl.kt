package com.github.klee0kai.thekey.app.ui.navigation.impl

import com.github.klee0kai.thekey.app.ui.navigation.PermissionsRouter
import com.github.klee0kai.thekey.app.ui.navigation.RouterContext
import kotlinx.coroutines.flow.Flow

class PermissionRouterImpl(context: RouterContext) : PermissionsRouter, RouterContext by context {

    override fun askPermissions(perms: Array<String>): Flow<Boolean> {
        TODO("Not yet implemented")
    }

}