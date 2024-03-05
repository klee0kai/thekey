package com.github.klee0kai.thekey.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NonRestartableComposable
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.navigation.NavigateResults.navChanges
import com.github.klee0kai.thekey.app.ui.navigation.NavigateResults.resultsScope
import com.github.klee0kai.thekey.app.utils.coroutine.awaitSec
import com.github.klee0kai.thekey.app.utils.coroutine.shareLatest
import dev.olshevski.navigation.reimagined.NavAction
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.NavEntry
import dev.olshevski.navigation.reimagined.NavId
import dev.olshevski.navigation.reimagined.navEntry
import dev.olshevski.navigation.reimagined.pop
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber

data class NavigateBackstackChange(
    val currentNavStack: List<NavEntry<*>>,
    val closedDestination: Pair<NavId, Any?>? = null,
) {
    val currentNavIds by lazy { currentNavStack.map { it.id } }
}

object NavigateResults {
    val navChanges = MutableSharedFlow<NavigateBackstackChange>(replay = 1)
    val resultsScope = DI.mainThreadScope()
}

suspend fun <T> NavController<T>.awaitScreenEvent(destination: T) {
    // wait screen open
    navChanges.filter { change ->
        change.currentNavStack.any { it.destination == destination }
    }.awaitSec()
    // wait screen close
    navChanges.first { change ->
        change.currentNavStack.all { it.destination != destination }
    }
}


@Composable
@NonRestartableComposable
fun <T> NavController<T>.cleanNotUselessResultFlows() {
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

inline fun <reified R> NavController<Destination>.navigateForResult(destination: Destination): Flow<R> {
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
        if (result.closedDestination?.first == navEntry.id) {
            (result.closedDestination.second as? R)?.let { emit(it) }
        }
    }.shareLatest(resultsScope)
}

fun <R> NavController<Destination>.backWithResult(
    result: R,
    exitFromApp: Boolean = false,
): Boolean {
    val navId = backstack.entries.last().id
    val popResult = pop()
    resultsScope.launch {
        navChanges.emit(
            NavigateBackstackChange(
                currentNavStack = backstack.entries,
                closedDestination = navId to result
            )
        )
    }
    if (!popResult && exitFromApp) {
        DI.backDispatcher().onBackPressed()
        return true
    }
    return popResult
}

fun NavController<Destination>.back() {
    DI.backDispatcher().onBackPressed()
}