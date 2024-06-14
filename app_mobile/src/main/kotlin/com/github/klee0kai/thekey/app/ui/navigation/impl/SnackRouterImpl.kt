package com.github.klee0kai.thekey.app.ui.navigation.impl

import androidx.compose.material3.SnackbarDuration
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.core.ui.navigation.RouterContext
import com.github.klee0kai.thekey.core.ui.navigation.SnackRouter
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SnackRouterImpl(val context: RouterContext) : SnackRouter, RouterContext by context {

    override fun snack(message: String, duration: SnackbarDuration) = scope.async {
        snackbarHostState.showSnackbar(
            message = message,
            duration = duration
        )
    }

    override fun snack(message: Int, duration: SnackbarDuration) = scope.launch {
        snackbarHostState.showSnackbar(
            message = DI.ctx().getString(message),
            duration = duration
        )
    }

}