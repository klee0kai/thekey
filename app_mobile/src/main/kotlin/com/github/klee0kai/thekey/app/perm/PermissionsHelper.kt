package com.github.klee0kai.thekey.app.perm

import com.github.klee0kai.thekey.core.perm.PermUnit
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.ui.navigation.model.TextProvider
import kotlinx.coroutines.flow.last

open class PermissionsHelper {

    /**
     * check all permissions is granted
     */
    open fun checkPermissions(perms: List<PermUnit>) = perms.all { it.isGranted() }

    /**
     * @return true is success
     */
    open suspend fun AppRouter.askPermissionsIfNeed(
        perms: List<PermUnit>,
        purpose: TextProvider,
        skipDialogs: Boolean = false,
    ): Boolean = perms.all { unit ->
        with(unit) {
            ask(purpose, skipDialog = skipDialogs)
                .last()
        }
    }

}