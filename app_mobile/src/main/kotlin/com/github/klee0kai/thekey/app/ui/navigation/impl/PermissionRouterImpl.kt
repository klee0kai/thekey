package com.github.klee0kai.thekey.app.ui.navigation.impl

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.github.klee0kai.thekey.app.ui.navigation.PermissionsRouter
import com.github.klee0kai.thekey.app.ui.navigation.RouterContext
import com.github.klee0kai.thekey.app.utils.coroutine.shareLatest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

class PermissionRouterImpl(context: RouterContext) : PermissionsRouter, RouterContext by context {

    override fun askPermissions(perms: Array<String>): Flow<Boolean> = callbackFlow {
        var reqPermLauncher: ActivityResultLauncher<Array<String>>? = null
        reqPermLauncher = activity?.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            scope.launch { send(result.all { it.value }) }
            reqPermLauncher?.unregister()
        }
        reqPermLauncher?.launch(perms)

        awaitClose()
    }.shareLatest(scope)
        .take(1)

}