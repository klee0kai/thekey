package com.github.klee0kai.thekey.app.ui.navigation.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NonRestartableComposable
import com.github.klee0kai.thekey.core.ui.navigation.ComposeRouter
import com.github.klee0kai.thekey.core.ui.navigation.RouterContext
import com.github.klee0kai.thekey.core.ui.navigation.model.Destination
import com.github.klee0kai.thekey.core.ui.navigation.model.DialogDestination
import com.github.klee0kai.thekey.core.ui.navigation.model.NavBoardDestination
import com.github.klee0kai.thekey.core.ui.navigation.model.NavigateBackstackChange
import com.github.klee0kai.thekey.core.utils.coroutine.awaitSec
import com.github.klee0kai.thekey.core.utils.coroutine.shareLatest
import dev.olshevski.navigation.reimagined.NavAction
import dev.olshevski.navigation.reimagined.navEntry
import dev.olshevski.navigation.reimagined.pop
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber

class ComposeRouterImpl(
    context: RouterContext,
) : ComposeRouter, RouterContext by context {

    override fun navigate(vararg destination: Destination): Flow<Any?> =
        navigate(destinations = destination, resultClazz = Any::class.java)

    override fun <R> navigate(vararg destinations: Destination, resultClazz: Class<R>): Flow<R> =
        navFullController.run {
            if (destinations.isEmpty()) return@run emptyFlow()
            val navEntries = destinations.map { navEntry(it) }.toList()
            val navEntry = navEntries.last()
            setNewBackstack(
                entries = backstack.entries + navEntries,
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
                if (result.closedDestination != null && result.closedDestination?.first == navEntry.id && resultClazz.isInstance(
                        result.closedDestination!!.second
                    )
                ) {
                    emit(result.closedDestination!!.second as R)
                }
            }.shareLatest(scope, resultClazz)
        }

    override fun <R> backWithResult(
        result: R,
        exitFromApp: Boolean,
    ): Boolean = navFullController.run {
        val navId = backstack.entries.lastOrNull()?.id ?: return false
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

    override fun back(exitFromApp: Boolean) = scope.launch {
        val popResult = navFullController.pop()

        if (!popResult && exitFromApp) {
            backDispatcher?.onBackPressed()
            return@launch
        }
    }

    override fun backFromDialog() = scope.launch {
        var entries = navFullController.backstack.entries.toList()
        val lastBoardIndex = entries.indexOfLast { it.destination is DialogDestination }
        if (lastBoardIndex > 0) {
            entries = entries.filterIndexed { index, _ -> index != lastBoardIndex }
            navFullController.setNewBackstack(entries)
        }
    }

    override fun backFromBoard() = scope.launch {
        var entries = navFullController.backstack.entries.toList()
        val lastBoardIndex = entries.indexOfLast { it.destination is NavBoardDestination }
        if (lastBoardIndex > 0) {
            entries = entries.filterIndexed { index, _ -> index != lastBoardIndex }
            navFullController.setNewBackstack(entries)
        }
    }

    override fun resetStack(vararg destinations: Destination) = scope.launch {
        val navEntries = destinations.map { navEntry(it) }.toList()
        navFullController.setNewBackstack(navEntries)
    }

    @Composable
    @NonRestartableComposable
    override fun collectBackstackChanges() {
        val backstackHash = navFullController.backstack.entries.map { it.id }.hashCode()
        LaunchedEffect(backstackHash) {
            navScreensController.setNewBackstack(
                navFullController.backstack.entries
                    .filter {
                        it.destination !is DialogDestination
                                && it.destination !is NavBoardDestination
                    }
            )

            navDialogsController.setNewBackstack(
                navFullController.backstack.entries
                    .takeLastWhile { it.destination is DialogDestination }
            )

            navBoardController.setNewBackstack(
                navFullController.backstack.entries
                    .takeLastWhile { it.destination is NavBoardDestination }
            )

        }

        LaunchedEffect(backstackHash) {
            delay(10)
            navChanges.emit(
                NavigateBackstackChange(
                    currentNavStack = navFullController.backstack.entries
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

    override suspend fun awaitScreenClose(destination: Destination) {
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