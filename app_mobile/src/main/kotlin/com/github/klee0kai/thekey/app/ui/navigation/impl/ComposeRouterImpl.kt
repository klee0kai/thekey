package com.github.klee0kai.thekey.app.ui.navigation.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NonRestartableComposable
import com.github.klee0kai.thekey.app.ui.navigation.ComposeRouter
import com.github.klee0kai.thekey.app.ui.navigation.RouterContext
import com.github.klee0kai.thekey.app.ui.navigation.model.Destination
import com.github.klee0kai.thekey.app.ui.navigation.model.NavigateBackstackChange
import com.github.klee0kai.thekey.app.utils.coroutine.awaitSec
import com.github.klee0kai.thekey.app.utils.coroutine.shareLatest
import dev.olshevski.navigation.reimagined.NavAction
import dev.olshevski.navigation.reimagined.navEntry
import dev.olshevski.navigation.reimagined.pop
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber
import kotlin.reflect.KClass

class ComposeRouterImpl(context: RouterContext) : ComposeRouter, RouterContext by context {

    override fun navigate(destination: Destination): Flow<Any?> = navigate(destination, Any::class)

    override fun <R : Any> navigate(destination: Destination, clazz: KClass<R>): Flow<R?> = composeController!!.run {
        val navEntry = navEntry(destination)
        setNewBackstack(
            entries = backstack.entries + navEntry,
            action = NavAction.Navigate
        )
        return flow<R> {
            // wait navigate to target
            withTimeoutOrNull(1000) {
                navChanges.first { change ->
                    change.currentNavIds.contains(navEntry.id)
                }
            }
            // wait close target
            val result = navChanges
                .first { change ->
                    val destClosed = change.closedDestination?.first == navEntry.id
                    val destinationLost = !change.currentNavIds.contains(navEntry.id)
                    if (destClosed || destinationLost) {
                        Timber.d("target ${navEntry.destination} id = ${navEntry.id} closed ($destClosed ; $destinationLost )")
                    }
                    destClosed || destinationLost
                }
            if (result.closedDestination?.first == navEntry.id && clazz.isInstance(result.closedDestination.second)) {
                emit(result.closedDestination.second as R)
            }
        }
    }.shareLatest(scope, clazz)

    override fun <R : Any> backWithResult(result: R, exitFromApp: Boolean): Boolean = composeController!!.run {
        val navId = backstack.entries.last().id
        val popResult = pop()
        scope.launch {
            navChanges.emit(
                NavigateBackstackChange(
                    currentNavStack = backstack.entries,
                    closedDestination = navId to result
                )
            )
        }
        if (!popResult && exitFromApp) {
            backDispatcher?.onBackPressed()
            return true
        }
        return popResult
    }

    override fun back() {
        backDispatcher?.onBackPressed()
    }

    @Composable
    @NonRestartableComposable
    override fun cleanNotUselessResultFlows() {
        composeController?.apply {
            LaunchedEffect(backstack.entries.map { it.id }.hashCode()) {
                delay(10)
                navChanges.emit(
                    NavigateBackstackChange(
                        currentNavStack = backstack.entries
                    )
                )
            }
            LaunchedEffect(Unit) {
                navChanges
                    .map { change -> change.currentNavStack.lastOrNull() }
                    .distinctUntilChanged { old, new -> old?.id == new?.id }
                    .collect { dest ->
                        Timber.d("open screen ${dest?.destination} id = ${dest?.id}")
                    }
            }
        }
    }

    override suspend fun awaitScreenEvent(destination: Destination) {
        // wait screen open
        navChanges.filter { change ->
            change.currentNavStack.any { it.destination == destination }
        }.awaitSec()
        // wait screen close
        navChanges.first { change ->
            change.currentNavStack.all { it.destination != destination }
        }
    }
}