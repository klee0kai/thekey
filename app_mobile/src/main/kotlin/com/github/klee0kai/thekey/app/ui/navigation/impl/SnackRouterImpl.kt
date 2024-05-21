package com.github.klee0kai.thekey.app.ui.navigation.impl

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.core.ui.navigation.RouterContext
import com.github.klee0kai.thekey.core.ui.navigation.SnackRouter

class SnackRouterImpl(val context: RouterContext) : SnackRouter, RouterContext by context {

    override suspend fun snack(message: String, duration: SnackbarDuration): SnackbarResult {
        return snackbarHostState.showSnackbar(
            message = message,
            duration = duration
        )
    }

    override suspend fun snack(message: Int, duration: SnackbarDuration) {
        snackbarHostState.showSnackbar(
            message = DI.ctx().getString(message),
            duration = duration
        )
    }

}