package com.github.klee0kai.thekey.app.ui.navigation.impl

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.github.klee0kai.thekey.app.ui.navigation.ActivityRouter
import com.github.klee0kai.thekey.app.ui.navigation.RouterContext
import com.github.klee0kai.thekey.app.ui.navigation.contracts.SimpleActivityContract
import com.github.klee0kai.thekey.app.utils.coroutine.shareLatest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

class ActivityRouterImpl(
    context: RouterContext
) : ActivityRouter, RouterContext by context {

    override fun navigate(intent: Intent): Flow<Intent?> = callbackFlow {
        var launcher: ActivityResultLauncher<Intent>? = null
        launcher = activity?.registerForActivityResult(SimpleActivityContract()) {
            scope.launch { send(it) }
            launcher?.unregister()
        }

        launcher?.launch(intent)

        awaitClose()
    }.shareLatest(scope)
        .take(1)


}