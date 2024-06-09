package com.github.klee0kai.thekey.core.ui.navigation

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedDispatcher
import androidx.annotation.StringRes
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import com.github.klee0kai.thekey.core.di.identifiers.ActivityIdentifier
import com.github.klee0kai.thekey.core.ui.navigation.deeplink.DeeplinkRoute
import com.github.klee0kai.thekey.core.ui.navigation.model.ActivityResult
import com.github.klee0kai.thekey.core.ui.navigation.model.Destination
import com.github.klee0kai.thekey.core.ui.navigation.model.NavigateBackstackChange
import com.github.klee0kai.thekey.core.ui.navigation.model.RequestPermResult
import com.github.klee0kai.thekey.core.utils.common.SafeContextScope
import com.github.klee0kai.thekey.core.utils.coroutine.completeAsync
import com.github.klee0kai.thekey.core.utils.coroutine.emptyJob
import dev.olshevski.navigation.reimagined.NavController
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow

interface AppRouter : RouterContext, DeeplinkRouter, ComposeRouter, SnackRouter, NavBoardRouter, ActivityRouter, PermissionsRouter

interface ComposeRouter {

    fun navigate(vararg destination: Destination): Flow<Any?> = emptyFlow()

    fun <R> navigate(vararg destinations: Destination, resultClazz: Class<R>): Flow<R?> = emptyFlow()

    fun <R> backWithResult(result: R, exitFromApp: Boolean = false): Boolean = false

    suspend fun awaitScreenClose(destination: Destination) = Unit

    fun back() = Unit

    fun resetStack(vararg destinations: Destination) = Unit

    @Composable
    fun collectBackstackChanges() = Unit

}

interface SnackRouter {

    fun snack(message: String, duration: SnackbarDuration = SnackbarDuration.Short): Deferred<SnackbarResult> = completeAsync(SnackbarResult.Dismissed)

    fun snack(@StringRes message: Int, duration: SnackbarDuration = SnackbarDuration.Short): Job = emptyJob()

}

interface NavBoardRouter {

    fun isNavigationBoardIsOpen(): Boolean = false

    suspend fun showNavigationBoard() = Unit

    suspend fun hideNavigationBoard() = Unit

}


interface ActivityRouter {

    fun navigate(intent: Intent): Flow<ActivityResult> = emptyFlow()

    fun onResult(result: ActivityResult) = Unit

}

interface PermissionsRouter {

    fun askPermissions(perms: Array<String>): Flow<Boolean> = emptyFlow()

    fun onResult(result: RequestPermResult) = Unit

}

interface DeeplinkRouter {

    suspend fun handleDeeplink(intent: Intent): Boolean = false

    fun configDeeplinks(block: DeeplinkRoute.() -> Unit) = Unit

}

interface RouterContext {

    /**
     * We show a stub while we initialize DI for a new feature
     */
    val showInitDynamicFeatureScreen: MutableStateFlow<Boolean> get() = MutableStateFlow(false)

    val activityIdentifier: ActivityIdentifier? get() = null

    val snackbarHostState: SnackbarHostState get() = SnackbarHostState()
    val navBoardState: DrawerState get() = DrawerState(DrawerValue.Closed)
    val navFullController: NavController<Destination> get() = TODO()
    val navScreensController: NavController<Destination> get() = TODO()
    val navDialogsController: NavController<Destination> get() = TODO()
    val activity: ComponentActivity? get() = TODO()

    val backDispatcher: OnBackPressedDispatcher? get() = TODO()
    val navChanges: MutableSharedFlow<NavigateBackstackChange> get() = TODO()

    val scope: SafeContextScope get() = TODO()

    fun genRequestCode(): Int = -1

    fun initDestination(dest: Destination) = Unit
}

inline fun <reified R> ComposeRouter.navigate(vararg destination: Destination): Flow<R?> =
    navigate(destinations = destination, resultClazz = R::class.java)

