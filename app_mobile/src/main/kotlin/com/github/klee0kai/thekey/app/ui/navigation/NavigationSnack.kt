package com.github.klee0kai.thekey.app.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import com.github.klee0kai.thekey.app.di.DI
import dev.olshevski.navigation.reimagined.NavController


suspend fun NavController<Destination>.snack(
    message: String,
    duration: SnackbarDuration = SnackbarDuration.Short,
): SnackbarResult {
    return DI.snackbarHostState()
        .showSnackbar(
            message = message,
            duration = duration
        )
}

suspend fun NavController<Destination>.snack(
    @StringRes message: Int,
    duration: SnackbarDuration = SnackbarDuration.Short,
): SnackbarResult {
    return DI.snackbarHostState().showSnackbar(
        message = DI.app().getString(message),
        duration = duration
    )
}