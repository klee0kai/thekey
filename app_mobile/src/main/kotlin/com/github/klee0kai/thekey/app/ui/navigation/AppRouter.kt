package com.github.klee0kai.thekey.app.ui.navigation

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedDispatcher
import androidx.annotation.StringRes
import androidx.compose.material3.DrawerState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import com.github.klee0kai.thekey.app.ui.navigation.model.ActivityResult
import com.github.klee0kai.thekey.app.ui.navigation.model.Destination
import com.github.klee0kai.thekey.app.ui.navigation.model.LoginDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.NavigateBackstackChange
import com.github.klee0kai.thekey.app.ui.navigation.model.RequestPermResult
import com.github.klee0kai.thekey.app.utils.common.SafeContextScope
import dev.olshevski.navigation.reimagined.NavController
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

interface AppRouter : RouterContext, ComposeRouter, SnackRouter, NavBoardRouter, ActivityRouter, PermissionsRouter {
    companion object {
        val InitDest = LoginDestination
    }
}

interface ComposeRouter {

    fun initIfNeed(destination: Destination)

    fun navigate(destination: Destination): Flow<Any?>

    fun <R> navigate(destination: Destination, clazz: Class<R>): Flow<R?>

    fun <R> backWithResult(result: R, exitFromApp: Boolean = false): Boolean

    suspend fun awaitScreenClose(destination: Destination)

    fun back()

    @Composable
    fun collectBackstackChanges()

}

interface SnackRouter {

    suspend fun snack(message: String, duration: SnackbarDuration = SnackbarDuration.Short): SnackbarResult

    suspend fun snack(@StringRes message: Int, duration: SnackbarDuration = SnackbarDuration.Short)

}

interface NavBoardRouter {

    fun isNavigationBoardIsOpen(): Boolean

    suspend fun showNavigationBoard()

    suspend fun hideNavigationBoard()

}


interface ActivityRouter {

    fun navigate(intent: Intent): Flow<ActivityResult>

    fun onResult(result: ActivityResult)

}

interface PermissionsRouter {

    fun askPermissions(perms: Array<String>): Flow<Boolean>

    fun onResult(result: RequestPermResult)

}

interface RouterContext {

    /**
     * We show a stub while we initialize DI for a new feature
     */
    val showInitDynamicFeatureScreen: MutableStateFlow<Boolean>

    val snackbarHostState: SnackbarHostState
    val navBoardState: DrawerState
    val navFullController: NavController<Destination>
    val navScreensController: NavController<Destination>
    val navDialogsController: NavController<Destination>
    val activity: ComponentActivity?

    val backDispatcher: OnBackPressedDispatcher?
    val navChanges: MutableSharedFlow<NavigateBackstackChange>

    val scope: SafeContextScope

    fun genRequestCode(): Int

}

inline fun <reified R> ComposeRouter.navigate(destination: Destination): Flow<R?> =
    navigate(destination, R::class.java)

