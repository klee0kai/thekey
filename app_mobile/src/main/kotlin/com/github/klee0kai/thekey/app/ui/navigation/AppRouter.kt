package com.github.klee0kai.thekey.app.ui.navigation

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedDispatcher
import androidx.annotation.StringRes
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import com.github.klee0kai.thekey.app.ui.navigation.model.Destination
import com.github.klee0kai.thekey.app.ui.navigation.model.NavigateBackstackChange
import dev.olshevski.navigation.reimagined.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlin.reflect.KClass

interface AppRouter : RouterContext, ComposeRouter, SnackRouter, ActivityRouter, PermissionsRouter

interface ComposeRouter {

    fun navigate(destination: Destination): Flow<Any?>

    fun <R : Any> navigate(destination: Destination, clazz: KClass<R>): Flow<R?>

    fun <R : Any> backWithResult(result: R, exitFromApp: Boolean = false): Boolean

    suspend fun awaitScreenEvent(destination: Destination)

    fun back()

    @Composable
    fun cleanNotUselessResultFlows()

}

interface SnackRouter {

    suspend fun snack(message: String, duration: SnackbarDuration = SnackbarDuration.Short): SnackbarResult

    suspend fun snack(@StringRes message: Int, duration: SnackbarDuration = SnackbarDuration.Short)

}


interface ActivityRouter {

    fun navigate(intent: Intent): Flow<Intent>

}

interface PermissionsRouter {

    fun askPermissions(perms: Array<String>): Flow<Boolean>

}

interface RouterContext {

    val snackbarHostState: SnackbarHostState
    val composeController: NavController<Destination>
    var activity: ComponentActivity?
    var backDispatcher: OnBackPressedDispatcher?

    val navChanges: MutableSharedFlow<NavigateBackstackChange>
    val scope: CoroutineScope

}

inline fun <reified R : Any> ComposeRouter.navigate(destination: Destination): Flow<R?> =
    navigate(destination, R::class)

